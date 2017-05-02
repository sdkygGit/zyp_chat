/*
 * @Title FileInfo.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-7 下午10:13:28
 * @version 1.0
 */
package com.download.entities;

import com.download.services.FileDownloadCallBack;

import java.io.Serializable;

/**
 * 文件信息
 * @author Yann
 * @date 2015-8-7 下午10:13:28
 */
public class FileInfo
{
	private long id;
	private String url;
	private String fileName;
	private int length;
	private int finished;

	private FileDownloadCallBack callBack;


	public FileInfo(){}

	/**
	 *@param id
	 *@param url
	 *@param fileName
	 *@param length
	 *@param finished
	 */
	public FileInfo(long id, String url, String fileName, int length,
					int finished,FileDownloadCallBack callBack)
	{
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
		this.callBack = callBack;
	}


	public FileDownloadCallBack getCallBack() {
		return callBack;
	}
	public void setCallBack(FileDownloadCallBack callBack) {
		this.callBack = callBack;
	}
	public long getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	public int getLength()
	{
		return length;
	}
	public void setLength(int length)
	{
		this.length = length;
	}
	public int getFinished()
	{
		return finished;
	}
	public void setFinished(int finished)
	{
		this.finished = finished;
	}
	@Override
	public String toString()
	{
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName
				+ ", length=" + length + ", finished=" + finished + "]";
	}





}
