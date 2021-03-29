package org.apromore.portal.config;

import java.util.ArrayList;
import java.util.List;

import org.apromore.plugin.Plugin;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.portal.servlet.PortalPluginResourceServlet;
import org.apromore.portal.util.ExplicitComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zkoss.spring.SpringUtil;

@Configuration
public class PortalWebConfig {

	@Autowired
	List<Plugin> portalPlugins;

	@Autowired(required = false)
	List<PortalProcessAttributePlugin> portalProcessAttributePlugin = new ArrayList<PortalProcessAttributePlugin>();

	@Autowired(required = false)
	List<FileImporterPlugin> fileImporterPlugins = new ArrayList<FileImporterPlugin>();
	
	@Autowired(required = false)
	List<EditorPlugin> editorPlugins = new ArrayList<EditorPlugin>();

	@Bean
	public List<EditorPlugin> editorPlugins() {
		return editorPlugins;
	}
	
	@Bean
	public List<FileImporterPlugin> fileImporterPlugins() {
		return fileImporterPlugins;
	}

	@Bean
	public List<PortalProcessAttributePlugin> portalProcessAttributePlugins() {
		return portalProcessAttributePlugin;
	}

	@Bean
	public List<Plugin> portalPlugins() {
		return portalPlugins;
	}

	@Bean
	public ExplicitComparator portalMenuOrder(@Value("${portal.menuorder}") String menuOrder) {
		return new ExplicitComparator(menuOrder);
	}

	@Bean
	public ExplicitComparator portalFileMenuitemOrder(@Value("${portal.menuitemorder.File}") String menuItemFile) {
		return new ExplicitComparator(menuItemFile);
	}

	@Bean
	public ServletRegistrationBean exampleServletBean() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new PortalPluginResourceServlet(),
				"/portalPluginResource/*");
		bean.setLoadOnStartup(1);
		return bean;
	}

}
