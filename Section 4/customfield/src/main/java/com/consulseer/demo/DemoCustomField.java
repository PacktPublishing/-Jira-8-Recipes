package com.consulseer.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import java.util.List;
import java.util.Map;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.security.JiraAuthenticationContext;

@Scanned
public class DemoCustomField extends GenericTextCFType {
    private static final Logger log = LoggerFactory.getLogger(DemoCustomField.class);
	private final JiraAuthenticationContext jiraAuthenticationContext;
	
    protected DemoCustomField(
			@JiraImport CustomFieldValuePersister customFieldValuePersister,
			@JiraImport GenericConfigManager genericConfigManager,
			@JiraImport TextFieldCharacterLengthValidator textFieldCharacterLengthValidator,
			@JiraImport JiraAuthenticationContext jiraAuthenticationContext) {
    super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator , jiraAuthenticationContext);
	this.jiraAuthenticationContext = jiraAuthenticationContext;
}
    
    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue,
                                                     final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        map.put("currentUser", jiraAuthenticationContext.getLoggedInUser().getName());

        return map;
    }

}