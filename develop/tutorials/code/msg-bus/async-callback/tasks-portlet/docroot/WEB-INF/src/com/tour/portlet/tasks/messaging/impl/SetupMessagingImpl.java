package com.tour.portlet.tasks.messaging.impl;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.service.ServiceContext;
import com.tour.portlet.tasks.service.SetupLocalServiceUtil;

public class SetupMessagingImpl implements MessageListener {

	public void receive(Message message) {
        try {
            doReceive(message);
        }
        catch (Exception e) {
            _log.error("Unable to process message " + message, e);
        }
    }

    protected void doReceive(Message message)
        throws Exception {
    	
    	// Receives message...
    	
    	String payload = (String)message.getPayload();
    	JSONObject jsonObject = JSONFactoryUtil.createJSONObject(payload);

    	long taskId = jsonObject.getLong("taskId");
    	String name = (String) jsonObject.getString("name");
        String description = (String) jsonObject.getString("description");
        String status = (String) jsonObject.getString("status");
        String responseDestinationName = jsonObject.getString(
                "responseDestinationName");
        
        // Create a new setup item...
        
        ServiceContext serviceContext = new ServiceContext();
        SetupLocalServiceUtil.addSetup(name, description, status, serviceContext);
        
        // Response...
        
        jsonObject = JSONFactoryUtil.createJSONObject();

        jsonObject.put("taskId", taskId);
        jsonObject.put("roadieResponse", "Yes");
        
        MessageBusUtil.sendMessage(
                responseDestinationName, jsonObject.toString());
        
    }

    private static Log _log = LogFactoryUtil.getLog(SetupMessagingImpl.class);

}
