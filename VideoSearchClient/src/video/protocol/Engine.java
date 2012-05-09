package video.protocol;

import org.ksoap2.serialization.SoapObject;

import android.R.string;
import android.util.Log;

public class Engine {

	public Good[] SearchByKeyFeatures(String featureCodes,String userPos,String alpha,String sameDegree,String kind) {
		featureCodes = featureCodes.trim().replace(" ", ",");
		Request r = new Request("SearchByKeyFeatures");
		r.put("featureCode",featureCodes);
		r.put("userPos","00");
		r.put("str_alpha",alpha);
		r.put("str_sameDegree", sameDegree);
		r.put("kind", kind);
		try {
			SoapObject soap = send(r);
			return toGoods(soap);
		} catch(Exception e){
			return null;
		}
	}
	
	private SoapObject send(Request r) throws Exception{
		return (SoapObject) r.GetResult().getProperty(0);
	}
	
	public Good[] SearchByKeyWords(String keyWords,String userPos,String kind){
		Request r = new Request("SearchByKeyWords");
		r.put("keyWords",keyWords);
		r.put("userPos",userPos);
		r.put("kind", kind);
		try{
			SoapObject detail = null;
			detail=(SoapObject)r.GetResult().getProperty(0);
			return toGoods(detail);
		}
		catch (Exception e) {
			Log.e("错误", e.getMessage());
			return null;
		}
	}
	
	/**登陆
	 * @param userName 用户名
	 * @param password 密码
	 * @return 返回结果，如果为ERROR
	 * @throws Exception 
	 */
	public String Login(String userName, String password) throws Exception{
		Request r = new Request("Login");
		r.put("userName", userName);
		r.put("password", password);
		SoapObject detail = r.GetResult();
		return getResult(detail,r);
	}
	
	public String Register(String userName, String password, String sex, String email) throws Exception{
		Request r = new Request("Register");
		r.put("username", userName);
		r.put("password", password);
		r.put("sex", sex);
		r.put("email", email);
		SoapObject detail = r.GetResult();
		return getResult(detail,r);
	}
	
	private String getResult(SoapObject soapObject,Request r){
	//	String str=soapObject.getProperty(0).toString();
		String resultString=soapObject.getProperty(r.getMethodName()+"Result").toString();
		return resultString;
	}
	
	private Good[] toGoods(SoapObject detail){
		Good[] goodEntities=new Good[detail.getPropertyCount()];
		int index=0;
		for(int i=0;i<detail.getPropertyCount();i++){
			SoapObject good=(SoapObject)detail.getProperty(i);
			Good geEntity=new Good();
			geEntity.setId(good.getProperty(0).toString());
			geEntity.setName(good.getProperty(1).toString());
			geEntity.setUrl(good.getProperty(2).toString());
			geEntity.setKind(good.getProperty(3).toString());
			geEntity.setPrice((Float.parseFloat(good.getProperty(4).toString())));
			geEntity.setPosition(good.getProperty(5).toString());
			geEntity.setGrade(good.getProperty(6).toString());
			geEntity.setFullUrl(good.getProperty(7).toString());
			geEntity.setDescribe((good.getProperty(8).toString()).endsWith("true")?true:false);
			geEntity.setRetire(good.getProperty(9).toString().endsWith("true")?true:false);
			geEntity.setExactPosition(good.getProperty(10).toString());
			goodEntities[index]=geEntity;
			index++;
		}
		return goodEntities;
	}
}
