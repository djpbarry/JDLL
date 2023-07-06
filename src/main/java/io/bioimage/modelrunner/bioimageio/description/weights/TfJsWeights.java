/*-
 * #%L
 * Use deep learning frameworks from Java in an agnostic and isolated way.
 * %%
 * Copyright (C) 2022 - 2023 Institut Pasteur and BioImage.IO developers.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.bioimage.modelrunner.bioimageio.description.weights;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that contains the information for Tensorflow Javascript weights.
 * For more information about the parameters go to:
 * https://github.com/bioimage-io/spec-bioimage-io/blob/gh-pages/weight_formats_spec_0_4.md
 * 
 * @author Carlos Garcia Lopez de Haro
 *
 */
public class TfJsWeights implements WeightFormat{

	/**
	 * Crate an object that specifies Tensorflow Javascript weights
	 * 
	 * @param weights
	 * 	part of the yaml file that contains exclusively the 
	 * 	information referring to the Keras weights
	 */
	public TfJsWeights(Map<String, Object> weights) {
		weightsFormat = "tensorflow_js";
		Set<String> keys = weights.keySet();
		for (String k : keys) {
			Object fieldElement = weights.get(k);
			switch (k)
	        {
            	case "tensorflow_version":
            		setTrainingVersion(fieldElement);
	                break;
	            case "source":
	                setSource(fieldElement);
	                break;
	            case "attachments":
	            	setAttachments(fieldElement);
	                break;
	            case "authors":
	                setAuthors(fieldElement);
	                break;
	            case "parent":
	                setParent(fieldElement);
	                break;
	            case "sha256":
	            	setSha256(fieldElement);
	                break;
	            case "architecture":
	            	setArchitecture(fieldElement);
	                break;
	            case "architecture_sha256":
	            	setArchitectureSha256(fieldElement);
	                break;
	        }
		}
	}

	private String weightsFormat;
	@Override
	public String getWeightsFormat() {
		return weightsFormat;
	}

	private String trainingVersion;
	@Override
	public String getTrainingVersion() {
		return trainingVersion;
	}
	
	/**
	 * Set the training version for the weights
	 * specified in the yaml if it exists
	 *
	 * @param v
	 * 	training version of the weights
	 */
	public void setTrainingVersion(Object v) {
		if (v instanceof String && !((String)v).contains("+")
				 && !((String)v).contains("cu")
				 && !((String)v).contains("cuda"))
			this.trainingVersion = (String) v;
		else if (v instanceof String && ((String)v).contains("+"))
			this.trainingVersion = ((String) v).substring(0, ((String) v).indexOf("+")).trim();
		else if (v instanceof String && ((String)v).contains("cuda"))
			this.trainingVersion = ((String) v).substring(0, ((String) v).indexOf("cuda")).trim();
		else if (v instanceof String && ((String)v).contains("cu"))
			this.trainingVersion = ((String) v).substring(0, ((String) v).indexOf("cu")).trim();
		else if (v instanceof Double)
			this.trainingVersion = "" + v;
		else if (v instanceof Float)
			this.trainingVersion = "" + v;
		else if (v instanceof Long)
			this.trainingVersion = "" + v;
		else if (v instanceof Integer)
			this.trainingVersion = "" + v;
	}

	private String sha256;
	@Override
	public String getSha256() {
		return sha256;
	}
	
	/**
	 * Set the SHA256 of the model from the parameters in the yaml
	 *
	 * @param s
	 * 	SHA256 of the model
	 */
	public void setSha256(Object s) {
		if (s instanceof String)
			sha256 = (String) s;
		
	}

	private String source;
	@Override
	public String getSource() {
		return source;
	}
	
	/**
	 * Set the source of the model from the parameters in the yaml
	 *
	 * @param s
	 * 	string from the yaml file containing the source, return only the
	 * 	name of the file inside the folder, not the whole path
	 */
	public void setSource(Object s) {
		if (s instanceof String)
			this.source = (String) s;
		
	}

	private List<String> authors;
	@Override
	public List<String> getAuthors() {
		return authors;
	}
	
	/**
	 * Set the authors of the model
	 * @param authors
	 * 	authors of the model
	 */
	public void setAuthors(Object authors) {
		if (authors instanceof String) {
			List<String> authList = new ArrayList<String>();
			authList.add((String) authors);
			this.authors = authList;
		} else if (authors instanceof List<?>) {
			this.authors = (List<String>) authors;
		}
		
	}

	private Map<String, Object> attachments;
	@Override
	public Map<String, Object> getAttachments() {
		return attachments;
	}
	
	/**
	 * Set the attachments of the weights if they exist
	 * @param attachments
	 * 	attachments of the model
	 */
	public void setAttachments(Object attachments) {
		if (attachments instanceof Map<?, ?>)
			this.attachments = (Map<String, Object>) attachments;
		
	}

	private String parent;
	@Override
	public String getParent() {
		return parent;
	}
	
	/**
	 * Set the parent of the weights in the case they exist
	 * @param parent
	 * 	parent weights of the model
	 */
	public void setParent(Object parent) {
		if (parent instanceof String)
			this.parent = (String) parent;
	}

	private String architecture;
	@Override
	public String getArchitecture() {
		return architecture;
	}
	
	/**
	 * Set the path to the architecture of the weights in the case it exists
	 * @param architecture
	 * 	path to the architecture of the model
	 */
	public void setArchitecture(Object architecture) {
		if (architecture instanceof String)
			this.architecture = (String) architecture;
	}

	private String architectureSha256;
	@Override
	public String getArchitectureSha256() {
		return architectureSha256;
	}
	
	/**
	 * Set the architecture Sha256 in the case it exists
	 * @param architectureSha256
	 * 	architecture Sha256 of the model
	 */
	public void setArchitectureSha256(Object architectureSha256) {
		if (architectureSha256 instanceof String)
			this.architectureSha256 = (String) architectureSha256;
	}

	@Override
	public String getSourceFileName() {
		if (source == null)
			return source;
		return new File(source).getName();
	}
	
	boolean gpu = false;
	/**
	 * Method to set whether the engine used for this weights supports GPU or not
	 * @param support
	 * 	whether the engine for the weights supports GPu or not
	 */
	@Override
	public void supportGPU(boolean support) {
		gpu = support;
	}
	
	/**
	 * Method to know whether the engine used for this weights supports GPU or not
	 * @return whether the engine for the weigths supports GPU or not
	 */
	@Override
	public boolean isSupportGPU() {
		return gpu;
	}


	private String compatibleVersion;
	@Override
	public String getJDLLCompatibleToTrainingVersion() {
		return compatibleVersion;
	}
}
