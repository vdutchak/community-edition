/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.repo.content.transform.magick.ImageMagickContentTransformer;
import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;

/**
 * Transfor action executer
 * 
 * @author Roy Wetherall
 */
public class ImageTransformActionExecuter extends TransformActionExecuter 
{
    /**
     * Action constants
     */
	public static final String NAME = "transform-image";
	public static final String PARAM_CONVERT_COMMAND = "convert-command";
	
	private static final String IMCOVERT = "imconvert ";
	private static final String SOURCE_TARGET =  " ${source} ${target}";
    
	private ImageMagickContentTransformer imageMagickContentTransformer;
	
	/**
	 * Set the image magick content transformer
	 * 
	 * @param imageMagickContentTransformer		the conten transformer
	 */
	public void setImageMagickContentTransformer(ImageMagickContentTransformer imageMagickContentTransformer) 
	{
		this.imageMagickContentTransformer = imageMagickContentTransformer;
	}
	
	/**
	 * Add parameter definitions
	 */
	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		super.addParameterDefintions(paramList);
		paramList.add(new ParameterDefinitionImpl(PARAM_CONVERT_COMMAND, ParameterType.STRING, false, getParamDisplayLabel(PARAM_CONVERT_COMMAND)));
	}
	
	/**
	 * @see org.alfresco.repo.rule.action.TransformActionExecuter#doTransform(org.alfresco.service.cmr.rule.RuleAction, org.alfresco.service.cmr.repository.ContentReader, org.alfresco.service.cmr.repository.ContentWriter)
	 */
	protected void doTransform(RuleAction ruleAction, ContentReader contentReader, ContentWriter contentWriter)
	{
		// Try and transform the content
        String convertCommand = (String)ruleAction.getParameterValue(PARAM_CONVERT_COMMAND);
        if (convertCommand != null && convertCommand.length() != 0)
        {
        	this.imageMagickContentTransformer.setConvertCommand(IMCOVERT + convertCommand + SOURCE_TARGET);
        }
        this.imageMagickContentTransformer.transform(contentReader, contentWriter);    
	}
}
