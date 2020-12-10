/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.impl;

import org.apromore.dao.*;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.util.AccessType;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor =
        Exception.class)
public class UserMetadataServiceImpl implements UserMetadataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImpl.class);

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private LogRepository logRepo;
    private GroupLogRepository groupLogRepo;
    private UserService userSrv;
    private GroupUsermetadataRepository groupUsermetadataRepo;
    private UsermetadataRepository userMetadataRepo;
    private UsermetadataTypeRepository usermetadataTypeRepo;
    private UsermetadataLogRepository usermetadataLogRepo;
    private UsermetadataProcessRepository usermetadataProcessRepo;
    private GroupRepository groupRepo;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param groupUserMetadataRepo Log repository.
     */
    @Inject
    public UserMetadataServiceImpl(final LogRepository logRepository,
                                   final GroupLogRepository groupLogRepository,
                                   final UserService userSrv,
                                   final GroupUsermetadataRepository groupUserMetadataRepo,
                                   final UsermetadataRepository userMetadataRepo,
                                   final UsermetadataTypeRepository usermetadataTypeRepo,
                                   final UsermetadataLogRepository usermetadataLogRepo,
                                   final UsermetadataProcessRepository usermetadataProcessRepo,
                                   final GroupRepository groupRepository) {
        this.logRepo = logRepository;
        this.groupLogRepo = groupLogRepository;
        this.userSrv = userSrv;
        this.groupUsermetadataRepo = groupUserMetadataRepo;
        this.userMetadataRepo = userMetadataRepo;
        this.usermetadataTypeRepo = usermetadataTypeRepo;
        this.usermetadataLogRepo = usermetadataLogRepo;
        this.usermetadataProcessRepo = usermetadataProcessRepo;
        this.groupRepo = groupRepository;
       
    }

    @Override
    @Transactional
    public void saveUserMetadataLinkedToOneLog(String userMetadataName, String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum,
                                               String username,
                                               Integer logId) throws UserNotFoundException {

        List<Integer> logIds = new LinkedList<>();
        logIds.add(logId);

        saveUserMetadata(userMetadataName, userMetadataContent, userMetadataTypeEnum, username, logIds);
    }

    @Override
    @Transactional
    public Usermetadata saveUserMetadata(String userMetadataName, String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum, String username,
                                 List<Integer> logIds) throws UserNotFoundException {

        //TODO:
        // 1. creator singleton group get Owner permission
        // 2. Log's GroupLogs get read permission
        // 3. Use transaction
        // 4. Pass in User directly

        User user = userSrv.findUserByLogin(username);

        Usermetadata userMetadata = new Usermetadata();

        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
//        Set<UsermetadataLog> usermetadataLogSet = userMetadata.getUsermetadataLog();
        Set<Log> logs = new HashSet<>();

        // Assign OWNER permission to the user's personal group
        groupUserMetadataSet.add(new GroupUsermetadata(user.getGroup(), userMetadata, true, true, true));

        for (Integer logId : logIds) {
            // Assign READ permission to all groups that have read permission to the linked artifact
//            for (GroupLog gl : groupLogRepo.findByLogId(logId)) {
//                if (gl.getHasRead() && !gl.getGroup().getName().equals(username)) { // exclude owner of user metadata
//                    groupUserMetadataSet.add(new GroupUsermetadata(gl.getGroup(), userMetadata, true, false, false));
//                }
//            }

            // Add linked artifact to the UsermetadataLog linked table
//            usermetadataLogSet.add(new UsermetadataLog(userMetadata, logRepo.findUniqueByID(logId)));
            logs.add(logRepo.findUniqueByID(logId));
        }

        // Assemble Usermetadata
        userMetadata.setGroupUserMetadata(groupUserMetadataSet);
//        userMetadata.setUsermetadataLog(usermetadataLogSet);
        userMetadata.setUsermetadataType(usermetadataTypeRepo.findOne(userMetadataTypeEnum.getUserMetadataTypeId()));
        userMetadata.setIsValid(true);
        userMetadata.setCreatedBy(user.getRowGuid());
        userMetadata.setCreatedTime(dateFormat.format(new Date()));
        userMetadata.setContent(userMetadataContent);
        userMetadata.setName(userMetadataName);
        userMetadata.setLogs(logs);

        // Persist Usermetadata, GroupUsermetadata and UsermetadataLog
        LOGGER.info("User: {} create user metadata ID: {} TYPE: {}.", username, userMetadata.getId(),
                userMetadataTypeEnum.toString());
        return userMetadataRepo.saveAndFlush(userMetadata);

    }

    /**
     *
     * Find UserMetadata that linked to specified Log and Group
     * @param logId logId
     * @param groupRowGuid GroupId
     * @return Set of GroupUsermetadata
     */
    private  Set<GroupUsermetadata> findByLogAndGroup(Integer logId, String groupRowGuid) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);
        Set<GroupUsermetadata> result = new HashSet<>();

        // All the user metadata that linked to this log
        Set<UsermetadataLog> usermetadataLogSet =
                new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

    
            for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                Usermetadata u = usermetadataLog.getUsermetadata();

                // Get all the user metadata that can be accessed by group
                List<GroupUsermetadata> groupUsermetadataList = groupUsermetadataRepo.findByUsermetadataId(u.getId());

                // Remove permissions assigned to specified group, and metadata itself
                for (GroupUsermetadata g : groupUsermetadataList) {
                    if (g.getGroup().equals(group)) {
                        result.add(g);
                    }
                }
            }

        return result;
    }

    @Override
    @Transactional
    public void saveUserMetadataAccessRightsByLogAndGroup(Integer logId, String groupRowGuid, AccessType accessType) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);

        // Assign specified group with the same permission to all the user metadata that linked to the specified log
        if (accessType.isRead() || accessType.isWrite() || accessType.isOwner()) {

            // All the user metadata that linked to this log
            Set<UsermetadataLog> usermetadataLogSet =
                    new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

            if (usermetadataLogSet.size() != 0) {

                for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                    Usermetadata u = usermetadataLog.getUsermetadata();
                    GroupUsermetadata g = groupUsermetadataRepo.findByGroupAndUsermetadata(group, u);

                    // Inherit permission from log
                    if (g == null) {
                        g = new GroupUsermetadata(group,
                                u, accessType.isRead(), accessType.isWrite(), accessType.isOwner());
                        u.getGroupUserMetadata().add(g);
                    } else {
                        g.setAccessRights(new AccessRights(accessType.isRead(), accessType.isWrite(), accessType.isOwner()));

                    }
                    groupUsermetadataRepo.save(g);
                    userMetadataRepo.save(u);
                }
            }

        }
    }

    @Override
    public void shareSimulationMetadata(Integer logId, String groupRowGuid, AccessType accessType) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);

        // Assign specified group with the same permission to the simulation metadata
        if (accessType.isRead() || accessType.isWrite() || accessType.isOwner()) {

            // All the user metadata that linked to this log
            Set<UsermetadataLog> usermetadataLogSet =
                    new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));

            if (usermetadataLogSet.size() != 0) {

                for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
                    Usermetadata u = usermetadataLog.getUsermetadata();

                    if (UserMetadataTypeEnum.SIMULATOR.getUserMetadataTypeId().equals(u.getUsermetadataType().getId())) {

                        GroupUsermetadata g = groupUsermetadataRepo.findByGroupAndUsermetadata(group, u);

                        // Inherit permission from log
                        if (g == null) {
                            g = new GroupUsermetadata(group,
                                    u, accessType.isRead(), accessType.isWrite(), accessType.isOwner());
                            u.getGroupUserMetadata().add(g);
                        } else {
                            g.setAccessRights(new AccessRights(accessType.isRead(), accessType.isWrite(), accessType.isOwner()));

                        }
                        groupUsermetadataRepo.save(g);
                        userMetadataRepo.save(u);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void removeUserMetadataAccessRightsByLogAndGroup(Integer logId, String groupRowGuid, String username) throws UserNotFoundException {

        Set<GroupUsermetadata> groupUsermetadataSet;

        groupUsermetadataSet = findByLogAndGroup(logId, groupRowGuid);

        for (GroupUsermetadata g : groupUsermetadataSet) {

            if(g.getUsermetadata().getGroupUserMetadata().size() == 1) { // if this metadata will be an orphan
                deleteUserMetadata(g.getUsermetadata().getId(), username);
            } else {
                groupUsermetadataRepo.delete(g);
            }
        }
    }

    @Override
    @Transactional
    public Usermetadata updateUserMetadata(Usermetadata userMetadata, String username, String content) throws UserNotFoundException, JpaOptimisticLockingFailureException {

        User user = userSrv.findUserByLogin(username);

        // Usermetadata userMetadata = userMetadataRepo.findOne(usermetadataId);
        userMetadata.setContent(content);
        userMetadata.setUpdatedBy(user.getRowGuid());
        userMetadata.setUpdatedTime(dateFormat.format(new Date()));

        // Persist Usermetadata
        LOGGER.info("User: {} update user metadata ID: {}.", username, userMetadata.getId());
        return userMetadataRepo.saveAndFlush(userMetadata);
    }

    @Override
    public Usermetadata updateUserMetadataName(Integer userMetadataId, String username, String name) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        // Optimistic locking version check is not necessary here since consistency should be guaranteed by EventQueue
        Usermetadata userMetadata = userMetadataRepo.findOne(userMetadataId);
        userMetadata.setName(name);
        userMetadata.setUpdatedBy(user.getRowGuid());
        userMetadata.setUpdatedTime(dateFormat.format(new Date()));

        // Persist Usermetadata
        LOGGER.info("User: {} update user metadata ID: {}.", username, userMetadata.getId());
        return userMetadataRepo.saveAndFlush(userMetadata);
    }

    @Override
    @Transactional
    public void deleteUserMetadata(Integer usermetadataId, String username) throws UserNotFoundException {

//        User user = userSrv.findUserByLogin(username);
//
//        Usermetadata userMetadata = userMetadataRepo.findById(usermetadataId);
//        userMetadata.setUpdatedBy(user.getRowGuid());
//        userMetadata.setUpdatedTime(dateFormat.format(new Date()));
//        userMetadata.setIsValid(false);
//
//        // Delete all UsermetadataLog
//        Set<UsermetadataLog> usermetadataLogSet = userMetadata.getUsermetadataLog();
//        usermetadataLogSet.clear();
//        userMetadata.setUsermetadataLog(usermetadataLogSet);
//        usermetadataLogRepo.delete(usermetadataLogSet);
//
//        // Delete all UsermetadataLog
//        Set<UsermetadataProcess> usermetadataProcessSet = userMetadata.getUsermetadataProcess();
//        usermetadataProcessSet.clear();
//        userMetadata.setUsermetadataProcess(usermetadataProcessSet);
//        usermetadataProcessRepo.delete(usermetadataProcessSet);
//
//        // Delete all GroupUsermetadata
//        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
//        groupUserMetadataSet.clear();
//        userMetadata.setGroupUserMetadata(groupUserMetadataSet);
//        groupUsermetadataRepo.delete(groupUserMetadataSet);
//
//        // Delete Usermetadata
//        userMetadataRepo.saveAndFlush(userMetadata);
        userMetadataRepo.delete(usermetadataId);
        LOGGER.info("User: {} Delete user metadata ID: {}.", username, usermetadataId);
    }

    @Override
    public Set<Usermetadata> getUserMetadataByUser(String username, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<Usermetadata> umSet = new HashSet<>();

        for (Group group : user.getGroups()) {
            List<GroupUsermetadata> guList = groupUsermetadataRepo.findByGroup(group);

            for (GroupUsermetadata gu : guList) {
                Usermetadata u = gu.getUsermetadata();
                if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                    umSet.add(u);
                }
            }
        }
        return umSet;
    }

    @Override
    public Set<Usermetadata> getUserMetadataByLogs(List<Integer> logIds, UserMetadataTypeEnum userMetadataTypeEnum) {

        Set<Usermetadata> result = new HashSet<>();

        if (null == logIds || logIds.size() == 0) {
            return result;
        }

        // Get all the user metadata that linked to specified logs
        List<Set<Usermetadata>> lists = new ArrayList<>();
        for (Integer logId : logIds) {
            lists.add(getUserMetadataByLog(logId, userMetadataTypeEnum));
        }
        // Find intersection of user metadata lists that get from specified logIds
        for (Set<Usermetadata> umSet : lists) {
            for (Usermetadata u : umSet) {
                int count = 0;
//                Set<UsermetadataLog> umlSet = u.getUsermetadataLog();
                Set<Log> logs = u.getLogs();
                if (logs.size() == logIds.size()) {  // May have duplicated UsermetadataLog umlSet.size()
                    for (Log l : logs) {
                        if (logIds.contains(l.getId())) {
                            count += 1;
                        }
                    }
                    if (count == logIds.size()) {
                        result.add(u);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Set<Usermetadata> getUserMetadataByLog(Integer logId, UserMetadataTypeEnum userMetadataTypeEnum) {

        Set<Usermetadata> umSet = new HashSet<>();
        Set<UsermetadataLog> usermetadataLogSet =
                new HashSet<>(usermetadataLogRepo.findByLog(logRepo.findUniqueByID(logId)));
        for (UsermetadataLog ul : usermetadataLogSet) {
            Usermetadata u = ul.getUsermetadata();
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                umSet.add(u);
            }
        }

        return umSet;
    }

    public Set<Usermetadata> getUserMetadataByUserAndLog(String username, Integer logId,
                                             UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        return getUserMetadata(username, Collections.singletonList(logId), userMetadataTypeEnum);
    }

    @Override
    public Set<Usermetadata> getUserMetadata(String username, List<Integer> logIds,
                                             UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException {

        Set<Usermetadata> umSetLinkedToUser = getUserMetadataByUser(username, userMetadataTypeEnum);
        Set<Usermetadata> umSetLinkedToLogs = getUserMetadataByLogs(logIds, userMetadataTypeEnum);

        // Find intersection of user metadata lists that get from 2 linked tables (log, group)
        umSetLinkedToUser.retainAll(umSetLinkedToLogs);

        return umSetLinkedToUser;
    }


    @Override
    public boolean canUserEditMetadata(String username, Integer usermetadataId) throws UserNotFoundException {

        for (GroupUsermetadata gl : groupUsermetadataRepo.findByLogAndUser(usermetadataId,
                userSrv.findUserByLogin(username).getRowGuid())) {
            if (gl.getHasOwnership()) { // Only owner can edit
                return true;
            }
        }
        return false;
    }

    @Override
    public AccessType getUserMetadataAccessType(Group group, Usermetadata usermetadata) {

        GroupUsermetadata gu = groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata);

        if (gu != null) {
            return AccessType.getAccessType(gu.getHasRead(), gu.getHasWrite(), gu.getHasOwnership());
        }
        return null;
    }

    @Override
    @Transactional
    public void saveUserMetadataWithoutLog(String content, UserMetadataTypeEnum userMetadataTypeEnum,
                                           String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        Usermetadata userMetadata = new Usermetadata();

        Set<GroupUsermetadata> groupUserMetadataSet = userMetadata.getGroupUserMetadata();
        Set<Log> logs = userMetadata.getLogs();

        // Assign OWNER permission to the user's personal group
        groupUserMetadataSet.add(new GroupUsermetadata(user.getGroup(), userMetadata, true, true, true));

        // Assign READ permission to all the groups that contain specified user
        for (Group group : user.getGroups()) {
            groupUserMetadataSet.add(new GroupUsermetadata(group, userMetadata, true, false, false));
        }

        // Assemble Usermetadata
        userMetadata.setGroupUserMetadata(groupUserMetadataSet);
        userMetadata.setLogs(logs);
        userMetadata.setUsermetadataType(usermetadataTypeRepo.findOne(userMetadataTypeEnum.getUserMetadataTypeId()));
        userMetadata.setIsValid(true);
        userMetadata.setCreatedBy(user.getRowGuid());
        userMetadata.setCreatedTime(dateFormat.format(new Date()));
        userMetadata.setContent(content);

        userMetadataRepo.saveAndFlush(userMetadata);
        LOGGER.info("User: {} create user metadata ID: {} TYPE: {}.", username,
                userMetadata.getId(), UserMetadataTypeEnum.DASH_TEMPLATE.toString());
    }

    @Override
    public Set<Usermetadata> getUserMetadataWithoutLog(UserMetadataTypeEnum userMetadataTypeEnum, String username) throws UserNotFoundException {

        User user = userSrv.findUserByLogin(username);

        // Get all the user metadata that can be accessed by groups that contain specified user
        Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
        for (Group group : user.getGroups()) {
            groupUsermetadataSet.addAll(groupUsermetadataRepo.findByGroup(group));
        }
        Set<Usermetadata> usermetadataList = new HashSet<>();
        for (GroupUsermetadata groupUsermetadata : groupUsermetadataSet) {
            Usermetadata u = groupUsermetadata.getUsermetadata();
            if (u.getUsermetadataType().getId().equals(userMetadataTypeEnum.getUserMetadataTypeId()) && u.getIsValid()) {
                usermetadataList.add(u);
            }
        }
        return usermetadataList.size() > 0 ? usermetadataList : null;
    }

    @Override
    public void deleteUserMetadataByLog(Log log, User user) throws UserNotFoundException {

        Set<UsermetadataLog> usermetadataLogSet =
                new HashSet<>(usermetadataLogRepo.findByLog(log));
        for (UsermetadataLog usermetadataLog : usermetadataLogSet) {
            deleteUserMetadata(usermetadataLog.getUsermetadata().getId(), user.getUsername());
        }
    }

    @Override
    public User findUserByRowGuid(String rowGuid) throws UserNotFoundException {
        return userSrv.findUserByRowGuid(rowGuid);
    }

    @Override
    public List<GroupUsermetadata> getGroupUserMetadata(Integer userMetadataId) {
        return groupUsermetadataRepo.findByUsermetadataId(userMetadataId);
    }

    @Override
    @Transactional
    public void saveUserMetadataAccessRights(Integer userMetadataId, String groupRowGuid, boolean hasRead,
                                             boolean hasWrite, boolean hasOwnership) {
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        Usermetadata usermetadata = userMetadataRepo.findById(userMetadataId);
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        GroupUsermetadata gu =
                groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata);

        if (gu == null) {
            gu = new GroupUsermetadata(group, usermetadata, accessRights);
        } else {
            gu.setAccessRights(accessRights);
        }
        groupUsermetadataRepo.save(gu);

        // Assign the same access right to logs if there is no access. Do nothing if there's already access.
        if (!canAccessAssociatedLog(userMetadataId, groupRowGuid)) {

            Set<Log> logs = findById(userMetadataId).getLogs();
            for (Log l : logs) {
                Set<Group> groups = new HashSet<>();

                for (GroupLog gl : l.getGroupLogs()) {
                    groups.add(gl.getGroup());
                }

                if(!groups.contains(group)) {
                    groupLogRepo.save(new GroupLog(group, l, accessRights));
                }
            }
        }
    }

    @Override
    public void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType) {

        saveUserMetadataAccessRights(userMetadataId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                accessType.isOwner());
    }

    @Override
    public void removeUserMetadataAccessRights(Integer userMetadataId, String groupRowGuid) {

        Group group = groupRepo.findByRowGuid(groupRowGuid);
        Usermetadata usermetadata = userMetadataRepo.findById(userMetadataId);

        groupUsermetadataRepo.delete(groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata));
    }

    @Override
    public Usermetadata findById(Integer id) {
        return userMetadataRepo.findById(id);
    }

    @Override
    public boolean canDeleteUserMetadata(Integer userMetadataId, String groupRowGuid) {

        List<GroupUsermetadata> groupUsermetadataList = getGroupUserMetadata(userMetadataId);
        List<GroupUsermetadata> ownerList = new ArrayList<>();

        // Using lambda here would cause runtime error
//        groupUsermetadataList =
//                groupUsermetadataList.stream()
//                        .filter(g -> g.getAccessRights().isOwnerShip())
//                        .collect(Collectors.toList());
//        return (groupUsermetadataList.size() == 1 && groupUsermetadataList.get(0).getGroup().getRowGuid().equals(groupRowGuid));


        for (GroupUsermetadata g :groupUsermetadataList) {
            if(g.getAccessRights().isOwnerShip()) {
                ownerList.add(g);
            }
        }

        return (ownerList.size() == 1 && ownerList.get(0).getGroup().getRowGuid().equals(groupRowGuid));
    }

    @Override
    public boolean canAccessAssociatedLog(Integer userMetadataId, String groupRowGuid) {

        Set<Log> logs = findById(userMetadataId).getLogs();

        // If this metadata doesn't link to log, then turn true.
        if(logs.size() == 0) {
            return true;
        }

        Set<Group> groups = new HashSet<>();
        Set<Group> intersection = new HashSet<>();
        for (Log l : logs) {
            // Assume groups can't be empty since this log is shown in UI, which means at least one user/group have
            // access to it.
            for (GroupLog gl : l.getGroupLogs()) {
                groups.add(gl.getGroup());
            }

            if (!intersection.isEmpty()) {
                groups.retainAll(intersection);
                intersection.clear();
            }

            intersection.addAll(groups);
            groups.clear();
        }

        return intersection.contains(groupRepo.findByRowGuid(groupRowGuid));
    }

    @Override
    public List<Log> getDependentLog(Usermetadata usermetadata) {
        Set<Log> logSet = usermetadata.getLogs();

        return new ArrayList<>(logSet);
    }

    @Override
    public List<Process> getDependentProcess(Usermetadata usermetadata) {
        List<Process> processes = new ArrayList<>();
        Set<UsermetadataProcess> usermetadataProcessSet = usermetadata.getUsermetadataProcess();

        for (UsermetadataProcess up : usermetadataProcessSet) {
            processes.add(up.getProcess());
        }

        return processes;
    }


}
