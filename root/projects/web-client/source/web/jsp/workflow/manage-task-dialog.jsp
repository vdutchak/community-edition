<%--
  Copyright (C) 2005 Alfresco, Inc.
 
  Licensed under the Mozilla Public License version 1.1 
  with a permitted attribution clause. You may obtain a
  copy of the License at
 
    http://www.alfresco.org/legal/license.txt
 
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  either express or implied. See the License for the specific
  language governing permissions and limitations under the
  License.
--%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="/WEB-INF/alfresco.tld" prefix="a" %>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="r" %>

<a:panel id="pooled-panel" label="#{msg.task_pooled_properties}" rendered="#{DialogManager.bean.pooledTask}" 
         border="white" bgcolor="white" titleBorder="lbgrey" expandedTitleBorder="dotted" titleBgcolor="white" styleClass="mainSubTitle">
   
   <r:propertySheetGrid id="pooled-task-props" value="#{DialogManager.bean.taskNode}" columns="1" >
      <r:property id="pooled-task-owner" readOnly="true" name="owner" />
      <r:association id="pooled-task-pool" readOnly="true" name="bpm:pooledActors" />
   </r:propertySheetGrid>

</a:panel>

<h:outputText id="padding1" styleClass="paddingRow" value="&nbsp;" escape="false" rendered="#{DialogManager.bean.pooledTask}" />

<a:panel id="metadata-panel" label="#{msg.task_properties}" 
         border="white" bgcolor="white" titleBorder="lbgrey" expandedTitleBorder="dotted" titleBgcolor="white" styleClass="mainSubTitle">
   
   <r:propertySheetGrid id="task-props" value="#{DialogManager.bean.taskNode}"
                       var="taskProps" columns="1" externalConfig="true" />
</a:panel>

<h:outputText id="padding2" styleClass="paddingRow" value="&nbsp;" escape="false" />

<a:panel id="resources-panel" label="#{msg.resources}"
         border="white" bgcolor="white" titleBorder="lbgrey" expandedTitleBorder="dotted" titleBgcolor="white" styleClass="mainSubTitle">
   
   <h:outputText value="#{msg.no_resources}" rendered="#{empty DialogManager.bean.resources}" />
   
   <a:richList id="resources-list" viewMode="details" value="#{DialogManager.bean.resources}" var="r"
               binding="#{DialogManager.bean.packageItemsRichList}"
               styleClass="recordSet" headerStyleClass="recordSetHeader" rowStyleClass="recordSetRow" 
               altRowStyleClass="recordSetRowAlt" width="100%" pageSize="10"
               initialSortColumn="name" initialSortDescending="true"
               rendered="#{not empty DialogManager.bean.resources}">
      
      <%-- Name column --%>
      <a:column id="col1" primary="true" width="200" style="padding:2px;text-align:left">
         <f:facet name="header">
            <a:sortLink id="col1-sort" label="#{msg.name}" value="name" mode="case-insensitive" styleClass="header"/>
         </f:facet>
         <f:facet name="small-icon">
               <a:actionLink id="col1-act1" value="#{r.name}" href="#{r.url}" target="new" image="#{r.fileType16}" 
                             showLink="false" styleClass="inlineAction" />
         </f:facet>
         <a:actionLink id="col1-act2" value="#{r.name}" href="#{r.url}" target="new" />
         <r:lockIcon id="col1-lock" value="#{r.nodeRef}" align="absmiddle" />
      </a:column>
      
      <%-- Description column --%>
      <a:column id="col2" style="padding:2px;text-align:left">
         <f:facet name="header">
            <a:sortLink id="col2-sort" label="#{msg.description}" value="description" styleClass="header"/>
         </f:facet>
         <h:outputText id="col2-txt" value="#{r.description}" />
      </a:column>
      
      <%-- Path column --%>
      <a:column id="col3" style="padding:2px;text-align:left">
         <f:facet name="header">
            <a:sortLink id="col3-sort" label="#{msg.path}" value="path" styleClass="header"/>
         </f:facet>
         <r:nodePath id="col3-path" value="#{r.path}" action="dialog:close:browse" 
                     actionListener="#{BrowseBean.clickSpacePath}" />
      </a:column>

      <%-- Created Date column --%>
      <a:column id="col4" style="padding:2px;text-align:left">
         <f:facet name="header">
            <a:sortLink id="col4-sort" label="#{msg.created}" value="created" styleClass="header"/>
         </f:facet>
         <h:outputText id="col4-txt" value="#{r.created}">
            <a:convertXMLDate type="both" pattern="#{msg.date_time_pattern}" />
         </h:outputText>
      </a:column>
      
      <%-- Modified Date column --%>
      <a:column id="col5" style="padding:2px;text-align:left">
         <f:facet name="header">
            <a:sortLink id="col5-sort" label="#{msg.modified}" value="modified" styleClass="header"/>
         </f:facet>
         <h:outputText id="col5-txt" value="#{r.modified}">
            <a:convertXMLDate type="both" pattern="#{msg.date_time_pattern}" />
         </h:outputText>
      </a:column>
                        
      <%-- Actions column --%>
      <a:column id="col6" actions="true" style="padding:2px;text-align:left">
         <f:facet name="header">
            <h:outputText id="col6-txt" value="#{msg.actions}"/>
         </f:facet>
         <r:actions id="col6-actions" value="#{DialogManager.bean.packageItemActionGroup}" 
                    context="#{r}" showLink="false" styleClass="inlineAction" />
      </a:column>
      <a:dataPager styleClass="pager" />
   </a:richList>

   <h:panelGrid id="package-actions-group" columns="1" styleClass="paddingRow">
      <r:actions id="package-actions" context="#{DialogManager.bean.taskNode}" 
                 value="#{DialogManager.bean.packageActionGroup}" />
   </h:panelGrid>
   
   <h:panelGrid id="add-item-control" columns="1" rendered="#{DialogManager.bean.itemBeingAdded}" 
                styleClass="selector" style="margin-top: 6px;">
      <r:contentSelector id="content-picker" value="#{DialogManager.bean.itemsToAdd}" styleClass="" />
      <h:panelGrid columns="2">
         <h:commandButton value="#{msg.add_to_list_button}" actionListener="#{DialogManager.bean.addPackageItems}" />
         <h:commandButton value="#{msg.cancel}" actionListener="#{DialogManager.bean.cancelAddPackageItems}" />
      </h:panelGrid>
   </h:panelGrid>

</a:panel>

<h:outputText id="padding3" styleClass="paddingRow" value="&nbsp;" escape="false" />

<a:panel id="workflow-summary-panel" label="#{msg.part_of_workflow}"
         border="white" bgcolor="white" titleBorder="lbgrey" expandedTitleBorder="dotted" titleBgcolor="white" styleClass="mainSubTitle">

   <r:workflowSummary id="workflow-summary" value="#{DialogManager.bean.workflowInstance}" styleClass="workflowSummary" />

</a:panel>

<h:outputText id="padding4" styleClass="paddingRow" value="&nbsp;" escape="false" />

<a:panel rendered="false" id="workflow-outline" label="#{msg.workflow_outline}" progressive="true" expanded="false" 
         border="white" bgcolor="white" titleBorder="lbgrey" expandedTitleBorder="dotted" titleBgcolor="white" styleClass="mainSubTitle">

   <h:graphicImage value="#{DialogManager.bean.workflowDefinitionImageUrl}"/>
   
</a:panel>
