//package com.lk.hotelcheck.network;
//
//import java.io.IOException;
//import java.net.URI;
//
//import org.apache.http.Header;
//import org.apache.http.HttpResponse;
//
//import com.loopj.android.http.ResponseHandlerInterface;
//
//public abstract class FileUploadHandle implements ResponseHandlerInterface{
//
//	public abstract void onStart();
//	public abstract void onProcess(int process, int total);
//	public abstract void onFinish();
//	public abstract void onFail();
//	
//	@Override
//	public Header[] getRequestHeaders() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public URI getRequestURI() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean getUseSynchronousMode() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onPostProcessResponse(ResponseHandlerInterface arg0,
//			HttpResponse arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPreProcessResponse(ResponseHandlerInterface arg0,
//			HttpResponse arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void sendCancelMessage() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void sendFailureMessage(int arg0, Header[] arg1, byte[] arg2,
//			Throwable arg3) {
//		onFail();
//	}
//
//	@Override
//	public void sendFinishMessage() {
//		onFinish();
//	}
//
//	@Override
//	public void sendProgressMessage(long arg0, long arg1) {
//		onProcess(arg0, arg1);
//	}
//
//	@Override
//	public void sendResponseMessage(HttpResponse arg0) throws IOException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void sendRetryMessage(int arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void sendStartMessage() {
//		onStart();
//	}
//
//	@Override
//	public void sendSuccessMessage(int arg0, Header[] arg1, byte[] arg2) {
//	}
//
//	@Override
//	public void setRequestHeaders(Header[] arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setRequestURI(URI arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setUseSynchronousMode(boolean arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
