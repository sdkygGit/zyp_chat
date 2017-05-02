/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wiz.dev.wiztalk.dto.model;

import java.io.Serializable;

public class MediaModel implements Serializable{

	public String url = null;
	public String displayName;
	public int size;
	public long addTime;
	public boolean isVideo;
	public boolean isImage;

	
	public MediaModel(String url,String displayName,int size,long addTime,boolean isVideo) {
		this.url = url;
		this.displayName = displayName;
		this.size = size;
		this.addTime = addTime;
		this.isVideo = isVideo;
	}

	public MediaModel(boolean isImage,String url,String displayName,int size,long addTime) {
		this.url = url;
		this.displayName = displayName;
		this.size = size;
		this.addTime = addTime;
		this.isImage = isImage;
	}
}
