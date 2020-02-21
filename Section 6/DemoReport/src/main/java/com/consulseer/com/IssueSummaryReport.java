package com.consulseer.com;

import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.web.action.ProjectActionSupport;

import java.util.Map;
import java.util.HashMap;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.apache.log4j.Logger;

import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.ParameterUtils;

import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.query.Query;

@Scanned
public class IssueSummaryReport extends AbstractReport {
	private static final Logger log = Logger.getLogger(IssueSummaryReport.class);
	
	@JiraImport private final SearchProvider searchProvider;
	@JiraImport private final ProjectManager projectManager;
	
	private Long projectId;
	private Long issuesCount, unassignedissueCount, assignedissueCount;
	
	public IssueSummaryReport(
		final SearchProvider searchProvider,
		final ProjectManager projectManager
	){
		this.searchProvider = searchProvider;
		this.projectManager = projectManager;
	}
		
    public String generateReportHtml(ProjectActionSupport projectActionSupport, Map map) throws Exception {
		
        // First lets get the name of the project from the configuration
        projectId = ParameterUtils.getLongParam(map, "selectedProjectId");
		
		//Lets find the Issue counts by status using JQL
		issuesCount = getOpenIssueCount(projectActionSupport.getLoggedInUser(), projectId);
		unassignedissueCount = getUnassignedIssueCount(projectActionSupport.getLoggedInUser(), projectId);
		assignedissueCount = getAssignedIssueCount(projectActionSupport.getLoggedInUser(), projectId);
		
		//Now, lets send it to the report so that it can be rendered on it
        Map<String, Object> velocityParams = new HashMap<>();
		
		velocityParams.put("projectName", projectManager.getProjectObj(projectId).getName());
		velocityParams.put("issueCount", issuesCount);
		velocityParams.put("unassignedissueCount", unassignedissueCount);
		velocityParams.put("assignedissueCount", assignedissueCount);
		return descriptor.getHtml("view", velocityParams);
		
    }
	
	private long getOpenIssueCount(ApplicationUser user, Long projectId) throws SearchException {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().project(projectId).buildQuery();
        return searchProvider.searchCount(query, user);
    }	
	
	private long getUnassignedIssueCount(ApplicationUser user, Long projectId) throws SearchException {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().assigneeIsEmpty().and().project(projectId).buildQuery();
        return searchProvider.searchCount(query, user);
    }	
	
	private long getAssignedIssueCount(ApplicationUser user, Long projectId) throws SearchException {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        Query query = queryBuilder.where().assigneeIsCurrentUser().and().project(projectId).buildQuery();
        return searchProvider.searchCount(query, user);
    }	
	
	
	
	@Override
	public void validate(ProjectActionSupport action, Map params) {
		
		 // First lets get the name of the project from the configuration
        projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
		
		if (projectId == null || this.projectManager.getProjectObj(projectId) == null){
			
			action.addError("projectId", action.getText("report.issuecreation.projectid.invalid"));
			log.error("Invalid projectId");
		}
		
		super.validate(action, params);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
