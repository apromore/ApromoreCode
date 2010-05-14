	
insert into users (lastname,firstname,email,username,passwd) values ("La Rosa", "Marcello", "m.larosa@qut.edu.au", "larosa", "");
insert into users (lastname,firstname,email,username,passwd) values ("Fauvet","Marie","marie-christine.fauvet@qut.edu.au","fauvet", "");
insert into users (lastname,firstname,email,username,passwd) values ("Ter Hofstede","Arthur","arthur@yawlfoundation.org","arthur", "");
insert into users (lastname,firstname,email,username,passwd) values ("Garcia-Banuelos","Luciano","lgbanuelos@gmail.com","luciano", "");
insert into users (lastname,firstname,email,username,passwd) values ("Dijkman","Remco","R.M.Dijkman@tue.nl","remco", "");

insert into canonicals (content) values ("pet1.cpf");
insert into canonicals (content) values ("pet2.cpf");
insert into canonicals (content) values ("pet3.cpf");
insert into canonicals (content) values ("pet4.cpf");
insert into canonicals (content) values ("invoice1.cpf");
insert into canonicals (content) values ("invoice2.cpf");
insert into canonicals (content) values ("shipment1.cpf");
insert into canonicals (content) values ("shipment2.cpf");
insert into canonicals (content) values ("pet5.cpf");
insert into canonicals (content) values ("payment1.cpf");
insert into canonicals (content) values ("payment2.cpf");
insert into canonicals (content) values ("pet6.cpf");
insert into canonicals (content) values ("pet7.cpf");
insert into canonicals (content) values ("pet8.cpf");
insert into canonicals (content) values ("pet9.cpf");
insert into canonicals (content) values ("pet10.cpf");
insert into canonicals (content) values ("pet11.cpf");
insert into canonicals (content) values ("pet12.cpf");
insert into canonicals (content) values ("pet13.cpf");
insert into canonicals (content) values ("pet14.cpf");
insert into canonicals (content) values ("LodgeClaim.cpf");
insert into canonicals (content) values ("QuoteRequest.cpf");
insert into canonicals (content) values ("pet15.cpf");
insert into canonicals (content) values ("pet16.cpf");
insert into canonicals (content) values ("pet17.cpf");
insert into canonicals (content) values ("pet18.cpf");
insert into canonicals (content) values ("pet19.cpf");
insert into canonicals (content) values ("payment3.cpf");
insert into canonicals (content) values ("payment4.cpf");
insert into canonicals (content) values ("payment5.cpf");
insert into canonicals (content) values ("LodgeClaim1.cpf");
insert into canonicals (content) values ("QuoteRequest1.cpf");
insert into canonicals (content) values ("LodgeClaim2.cpf");
insert into canonicals (content) values ("QuoteRequest2.cpf");
insert into canonicals (content) values ("LodgeClaim3.cpf");
insert into canonicals (content) values ("QuoteRequest3.cpf");

insert into native_types values ("EPML 2.0");
insert into native_types values ("XPDL 2.1");
insert into native_types values ("BPMN 2.0");
insert into native_types values ("YAWL 2.0");
insert into native_types values ("BPEL 2.0");
insert into native_types values ("Protos 8.0.2");
insert into native_types values ("PNML 1.3.2");

insert into natives (canonical,nat_type) values (1,"EPML 2.0");
insert into natives (canonical,nat_type) values (2,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (3,"YAWL 2.0");
insert into natives (canonical,nat_type) values (4,"EPML 2.0");
insert into natives (canonical,nat_type) values (5,"YAWL 2.0");
insert into natives (canonical,nat_type) values (6,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (7,"XPDL 2.1");
insert into natives (canonical,nat_type) values (8,"XPDL 2.1");
insert into natives (canonical,nat_type) values (9,"XPDL 2.1");
insert into natives (canonical,nat_type) values (10,"YAWL 2.0");
insert into natives (canonical,nat_type) values (11,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (12,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (13,"EPML 2.0");
insert into natives (canonical,nat_type) values (14,"YAWL 2.0");
insert into natives (canonical,nat_type) values (15,"XPDL 2.1");
insert into natives (canonical,nat_type) values (16,"YAWL 2.0");
insert into natives (canonical,nat_type) values (17,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (18,"XPDL 2.1");
insert into natives (canonical,nat_type) values (19,"XPDL 2.1");
insert into natives (canonical,nat_type) values (20,"XPDL 2.1");
insert into natives (canonical,nat_type) values (21,"XPDL 2.1");
insert into natives (canonical,nat_type) values (22,"YAWL 2.0");
insert into natives (canonical,nat_type) values (23,"XPDL 2.1");
insert into natives (canonical,nat_type) values (24,"XPDL 2.1");
insert into natives (canonical,nat_type) values (25,"YAWL 2.0");
insert into natives (canonical,nat_type) values (26,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (27,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (28,"Protos 8.0.2");
insert into natives (canonical,nat_type) values (29,"XPDL 2.1");
insert into natives (canonical,nat_type) values (30,"XPDL 2.1");
insert into natives (canonical,nat_type) values (31,"YAWL 2.0");
insert into natives (canonical,nat_type) values (32,"XPDL 2.1");
insert into natives (canonical,nat_type) values (33,"XPDL 2.1");
insert into natives (canonical,nat_type) values (34,"YAWL 2.0");

insert into processes (name, domain, owner, original_type) values ("Pet registration - Cairns","Pet laws",1,"EPML 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet registration - Cairns","Pet laws",1,"YAWL 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet registration - Sydney","Pet laws",2,"Protos 8.0.2");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Cairns","Pet laws",1,"Protos 8.0.2");
insert into processes (name, domain, owner, original_type) values ("Pet registration - Sydney","Pet laws",1,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Shipment","Order management",2,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Pet registration - Sydney","Pet laws",2,"YAWL 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet registration - Sydney","Pet laws",2,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Pet registration - Sydney","Pet laws",3,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Invoicing","Order management",3,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Cairns","Pet laws",3,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Townville","Pet laws",3,"YAWL 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Brisbane","Pet laws",2,"Protos 8.0.2");
insert into processes (name, domain, owner, original_type) values ("On line payment","Order management",1,"EPML 2.0");
insert into processes (name, domain, owner, original_type) values ("On line payment","Order management",1,"EPML 2.0");
insert into processes (name, domain, owner, original_type) values ("Lodge a claim", "Insurance", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Request a quote", "Commerce", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Cairns","Pet laws",3,"XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Townville","Pet laws",3,"YAWL 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Brisbane","Pet laws",2,"Protos 8.0.2");
insert into processes (name, domain, owner, original_type) values ("On line payment","Order management",1,"EPML 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Brisbane","Pet laws",2,"Protos 8.0.2");
insert into processes (name, domain, owner, original_type) values ("On line payment","Order management",1,"EPML 2.0");
insert into processes (name, domain, owner, original_type) values ("Pet permit - Brisbane","Pet laws",2,"Protos 8.0.2");
insert into processes (name, domain, owner, original_type) values ("On line payment","Order management",1,"EPML 2.0");
insert into processes (name, domain, owner, original_type) values ("Lodge a claim", "Insurance", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Request a quote", "Commerce", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Lodge a claim", "Insurance", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Request a quote", "Commerce", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Lodge a claim", "Insurance", 4, "XPDL 2.1");
insert into processes (name, domain, owner, original_type) values ("Request a quote", "Commerce", 4, "XPDL 2.1");

insert into process_versions values (1,"v0",date_format("2009-02-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),1,2);
insert into process_versions values (1,"v1",date_format("2010-01-24 8:55:01","%Y-%m-%d %H:%i:%s"),now(),2,4);
insert into process_versions values (1,"v2",date_format("2010-01-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),3,2);
insert into process_versions values (2,"v0",date_format("2010-01-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),4,3);
insert into process_versions values (3,"v0",date_format("2010-01-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),5,2);
insert into process_versions values (4,"v0",date_format("2010-01-22 8:55:01","%Y-%m-%d %H:%i:%s"),now(),6,1);
insert into process_versions values (4,"v1",date_format("2010-01-24 8:55:01","%Y-%m-%d %H:%i:%s"),now(),7,1);
insert into process_versions values (4,"v2",date_format("2010-01-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),8,2);
insert into process_versions values (5,"v0",date_format("2010-02-20 8:55:01","%Y-%m-%d %H:%i:%s"),now(),9,3);
insert into process_versions values (6,"v0",date_format("2010-02-21 8:55:01","%Y-%m-%d %H:%i:%s"),now(),10,4);
insert into process_versions values (7,"v0",date_format("2010-02-22 8:55:01","%Y-%m-%d %H:%i:%s"),now(),11,3);
insert into process_versions values (8,"v0",date_format("2010-02-25 9:55:01","%Y-%m-%d %H:%i:%s"),now(),12,3);
insert into process_versions values (9,"v0",date_format("2010-01-21 8:55:11","%Y-%m-%d %H:%i:%s"),now(),13,2);
insert into process_versions values (9,"v1",date_format("2010-02-20 8:55:01","%Y-%m-%d %H:%i:%s"),now(),14,1);
insert into process_versions values (10,"v0",date_format("2008-02-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),15,1);
insert into process_versions values (11,"v0",date_format("2009-12-25 8:55:01","%Y-%m-%d %H:%i:%s"),now(),16,3);
insert into process_versions values (12,"v0",date_format("2010-01-21 8:55:01","%Y-%m-%d %H:%i:%s"),now(),17,3);
insert into process_versions values (13,"v0",date_format("2010-01-22 8:55:01","%Y-%m-%d %H:%i:%s"),now(),18,4);
insert into process_versions values (14,"v0",date_format("2010-01-19 8:55:01","%Y-%m-%d %H:%i:%s"),now(),19,4);
insert into process_versions values (15,"v0",date_format("2010-01-16 8:55:01","%Y-%m-%d %H:%i:%s"),now(),20,4);
insert into process_versions values (16, "v0", date_format("2010-03-11 8:55:01","%Y-%m-%d %H:%i:%s"),now(),21,5);
insert into process_versions values (17, "v0", date_format("2010-03-11 8:55:01","%Y-%m-%d %H:%i:%s"),now(),22,5);
insert into process_versions values (18,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),23,0);
insert into process_versions values (19,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),24,0);
insert into process_versions values (20,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),25,0);
insert into process_versions values (21,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),26,4);
insert into process_versions values (22,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),27,0);
insert into process_versions values (23,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),28,4);
insert into process_versions values (24,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),29,0);
insert into process_versions values (25,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),30,4);
insert into process_versions values (26,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),31,4);
insert into process_versions values (27,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),32,0);
insert into process_versions values (28,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),33,4);
insert into process_versions values (29,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),34,0);
insert into process_versions values (30,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),35,4);
insert into process_versions values (31,"v0",date_format("2010-03-18 8:55:01","%Y-%m-%d %H:%i:%s"),now(),36,4);

insert into search_histories(userId,search) values (1, "yawl");
insert into search_histories(userId,search) values (1, "bpmn");
insert into search_histories(userId,search) values (2, "yaw,protos, pet law");
select * from process_versions;
