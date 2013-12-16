package com.twlkyao.filecloud;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

/**
 * Upload file to the cloud and download the file from the cloud
 * @author Shiyao Qi
 */
public class MainActivity extends Activity {

	private String cloudUrl; // The url of the cloud
	
	private TextView uploadTextView; // The upload textview for upload url.
	private TextView downloadTextView; // The download textview for download url.
	
	private String uploadUrl; // The upload url string.
	private String downloadUrl; // The download url string.
	
	private Button btn_upload; // The upload button.
	private Button btn_download; // The download button.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViews(); // Find the views for use.
		setListeners(); // Set the listeners.
	}

	/**
	 * Find the views by id.
	 */
	public void findViews() {
		uploadTextView = (TextView) this.findViewById(R.id.upload_url);
		downloadTextView = (TextView) this.findViewById(R.id.download_url);
		
		btn_upload = (Button) this.findViewById(R.id.btn_upload);
		btn_download = (Button) this.findViewById(R.id.btn_download);
	}

	public void setListeners() {
		btn_upload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadUrl = uploadTextView.getText().toString(); // Get the url of the local file.
			}
		});
		
		btn_download.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	/**
	 * Upload the file to specified url.
	 * @param strUplodFileUrl The url of the file storage server.
	 * @param filepath The filepath of the local file.
	 * @param username The username on the storage server.
	 * @param password The password for the user.
	 * @return The upload status.
	 */
	public boolean uploadFile(String strUploadFileUrl, String filepath, String username, String password) {
		
		boolean status = false; // Indicate the upload status
		
		String end = "\r\n";  
		String twoHyphens = "--";  
		String boundary = "******";  
		try 
		{
			URL url = new URL(strUploadFileUrl); // Construct a url
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); // Open a connection	
			// Set the size of the stream in case it failed due to the small memory
			// This method use the without buffering HTTP stream to request the content without known the size
		      httpURLConnection.setChunkedStreamingMode(128 * 1024);// The size of the stream is 128K
		      
		      // Set the inputstream and outputstream
		      httpURLConnection.setDoInput(true); // Allow input
		      httpURLConnection.setDoOutput(true); // Allow output
		      httpURLConnection.setUseCaches(false); // Do not use cache
		      
		      // Use the HTTP post method
		      httpURLConnection.setRequestMethod("POST");  
		      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
		      httpURLConnection.setRequestProperty("Charset", "UTF-8");  
		      httpURLConnection.setRequestProperty("Content-Type",  
		          "multipart/form-data;boundary=" + boundary);  
		  
		      DataOutputStream dos = new DataOutputStream(  
		          httpURLConnection.getOutputStream());  
		      dos.writeBytes(twoHyphens + boundary + end);  
		      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""  
		          + filepath.substring(filepath.lastIndexOf("/") + 1)  
		          + "\""  
		          + end);  
		      dos.writeBytes(end);  
		  
		      FileInputStream fis = new FileInputStream(filepath);  
		      byte[] buffer = new byte[8192]; // 8k  
		      int count = 0;  
		      // 读取文件  
		      while ((count = fis.read(buffer)) != -1)  
		      {  
		        dos.write(buffer, 0, count);  
		      }  
		      fis.close();  
		  
		      dos.writeBytes(end);  
		      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
		      dos.flush();  
		  
		      InputStream is = httpURLConnection.getInputStream();  
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");  
		      BufferedReader br = new BufferedReader(isr);  
		      String result = br.readLine();
		      
		      System.out.println("result:" + result);
		  
//		      Toast.makeText(this, result, Toast.LENGTH_LONG).show();  
		      dos.close();  
		      is.close();  
		      status = true;
		  
		    } catch (Exception e)  
		    {  
		      e.printStackTrace();  
		    }
			return status;  
	}
	
	/**
	 * Download file from the specified url.
	 * @param strDownloadFileUrl The url of the file storage server.
	 * @param filepath The filepath of the downloaded file to save.
	 * @param username The username on the storage server.
	 * @param password The password for the user.
	 * @return The download status.
	 */
	public boolean downloadFile(String strDownloadFileUrl, String filepath, String username, String password) {
		boolean status = false; // Indicate the download status.
		String message = ""; // To record the related message for the function.
		if(!URLUtil.isNetworkUrl(strDownloadFileUrl)) { // The url of the server is error.
			message += "Wrong url";
		} else { // The url of the server is correct.
			try {
				URL url = new URL(strDownloadFileUrl); // Construct a url.
				URLConnection conn = url.openConnection(); // Open a connection.
				conn.connect();
				
				InputStream is = conn.getInputStream(); // Get an InputStream instance to download file.
				if(null == is) {
					throw new RuntimeException("stream is null");
				} 
				
				File file = new File(Environment.getExternalStorageDirectory() + filepath); // Construct a file according to the filepath.
				
//				String tempFilePath = file.getAbsolutePath(); // Get the temporary filepath.
				
				FileOutputStream fos = new FileOutputStream(file); // Construct a FileOutputStream to write file.
				byte buffer[] = new byte[1024]; // File write buffer.
				
				while(-1 != is.read(buffer)) { // Read to the buffer.
					fos.write(buffer); // Write to the file.
				}
				/*do {
					int read = is.read(buffer); // Read the file into buffer
					if(read < 0) {
						break;
					}
					fos.write(buffer, 0 ,read);
				} while(true);*/
				
				is.close(); // Close the InputStream when the writing is done.
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return true;
	}
}
