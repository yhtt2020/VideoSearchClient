package video.protocol;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class Request {
	// �����ռ�
	final static String SERVICE_NS = "http://yhtt2020.vicp.cc/VideoSearchService";
	// �����ַ
	final static String SERVICE_URL = "http://www.coolsou.com/SearchEngine.asmx";
	// ACTION
	final String SERVICE_ACTION = SERVICE_NS;
	private String methodName = "";
	private SoapObject soapObject = null;

	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param operation
	 *            �������ƣ���Ӧ��webservice�ĺ���
	 */
	public Request(String operation) {
		this.methodName = operation;
		soapObject = new SoapObject(SERVICE_NS, operation);
	}

	/**
	 * ʹ���������Եķ�ʽ��Ӳ��������˳������Ʊ�����webservice��ȫ��ͬ
	 * 
	 * @param name
	 *            ����ӵĲ�������
	 * @param value
	 *            ����Ӳ�����ֵ
	 */
	public void put(String name, Object value) {
		soapObject.addProperty(name, value);
	}

	/**
	 * ʹ��propertyinfo��ʽ��Ӳ���
	 * 
	 * @param pi
	 *            propertyinfo
	 */
	public void AddPropertyParameter(PropertyInfo pi) {
		soapObject.addProperty(pi);
	}

	/**
	 * ���������ʽ�Ĳ�����һ�㲻ʹ�����ַ���
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public void AddAttributeParameter(String name, Object value) {
		soapObject.addAttribute(name, value);
	}

	/**
	 * ����HttpTransferportSE���÷�����˽�з���
	 * 
	 * @return ����һ��HttpTransferportSE����
	 */
	private HttpTransportSE CreateTransportSE() {
		HttpTransportSE httpTransportSE = new HttpTransportSE(SERVICE_URL);
		httpTransportSE.debug = true;
		return httpTransportSE;
	}

	/**
	 * ����һ�����л����Soap������÷�����˽�з���
	 * 
	 * @return
	 */
	private SoapSerializationEnvelope CreateEnvelope() {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		return envelope;
	}

	/**
	 * ȥ��SoapObject����ͨ���ö����ȡ�ÿɲ����Խ�ǿ�Ľ��
	 * 
	 * @return �������
	 */
	public SoapObject GetResult() throws Exception {
		String methodNameString = soapObject.getName();
		SoapObject resultObject = null;
		SoapSerializationEnvelope envelope = CreateEnvelope();

		HttpTransportSE se = CreateTransportSE();
		// se.call(null, envelope);
		se.call(SERVICE_ACTION + "/" + methodNameString, envelope);

		if (envelope.getResponse() != null) {
			// �����Ϊ�գ�ֱ�ӷ���Soap����
			resultObject = (SoapObject) envelope.bodyIn;

			return resultObject;
		}
		// ���Ϊ��

		//
		return resultObject;
	}

	public String getStringResult(SoapObject soapObject, Request r) {
		// String str=soapObject.getProperty(0).toString();
		String resultString = soapObject.getProperty(
				r.getMethodName() + "Result").toString();
		return resultString;
	}
}
