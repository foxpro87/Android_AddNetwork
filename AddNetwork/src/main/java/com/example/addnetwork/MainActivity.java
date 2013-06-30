package com.example.addnetwork;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private static final String INT_PRIVATE_KEY = "private_key";
    private static final String INT_PHASE2 = "phase2";
    private static final String INT_PASSWORD = "password";
    private static final String INT_IDENTITY = "identity";
    private static final String INT_EAP = "eap";
    private static final String INT_CLIENT_CERT = "client_cert";
    private static final String INT_CA_CERT = "ca_cert";
    private static final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
    final String INT_ENTERPRISEFIELD_NAME = "android.net.wifi.WifiConfiguration$EnterpriseField";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiConfiguration wc = new WifiConfiguration();
//        wc.SSID = "\"WORK\"";
//        wc.preSharedKey  = "\"!!TAG-B001\"";
//        wc.hiddenSSID = true;
//        wc.status = WifiConfiguration.Status.ENABLED;
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//        //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
//        //wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//        //wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        int res = wifi.addNetwork(wc);
//        Log.d("sbwoo", "add Network returned " + res );
//        boolean b = wifi.enableNetwork(res, true);
//        Log.d("sbwoo", "enableNetwork returned " + b );


        saveEapConfig("!!TAG-B001", "TAG-B001");



    }


    void saveEapConfig(String passString, String userName)
    {
        /********************************Configuration Strings****************************************************/
        final String ENTERPRISE_EAP = "PEAP";
        final String ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_CertificateName";
        final String ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_CertificateName";
        //CertificateName = Name given to the certificate while installing it

    /*Optional Params- My wireless Doesn't use these*/
        final String ENTERPRISE_PHASE2 = "";
        final String ENTERPRISE_ANON_IDENT = "";
        final String ENTERPRISE_CA_CERT = "";
        /********************************Configuration Strings****************************************************/

    /*Create a WifiConfig*/
        WifiConfiguration selectedConfig = new WifiConfiguration();

    /*AP Name*/
        selectedConfig.SSID = "\"WORK\"";

    /*Priority*/
        selectedConfig.priority = 40;

    /*Enable Hidden SSID*/
        selectedConfig.hiddenSSID = true;

    /*Key Mgmnt*/
        selectedConfig.allowedKeyManagement.clear();
        selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

    /*Group Ciphers*/
        selectedConfig.allowedGroupCiphers.clear();
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

    /*Pairwise ciphers*/
        selectedConfig.allowedPairwiseCiphers.clear();
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

    /*Protocols*/
        selectedConfig.allowedProtocols.clear();
        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        // Enterprise Settings
        // Reflection magic here too, need access to non-public APIs
        try {
            // Let the magic start
            Class[] wcClasses = WifiConfiguration.class.getClasses();
            // null for overzealous java compiler
            Class wcEnterpriseField = null;

            for (Class wcClass : wcClasses)
                if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME))
                {
                    wcEnterpriseField = wcClass;
                    break;
                }
            boolean noEnterpriseFieldType = false;
            if(wcEnterpriseField == null)
                noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

            Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
            Field[] wcefFields = WifiConfiguration.class.getFields();
            // Dispatching Field vars
            for (Field wcefField : wcefFields)
            {
                if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                    wcefAnonymousId = wcefField;
                else if (wcefField.getName().equals(INT_CA_CERT))
                    wcefCaCert = wcefField;
                else if (wcefField.getName().equals(INT_CLIENT_CERT))
                    wcefClientCert = wcefField;
                else if (wcefField.getName().equals(INT_EAP))
                    wcefEap = wcefField;
                else if (wcefField.getName().equals(INT_IDENTITY))
                    wcefIdentity = wcefField;
                else if (wcefField.getName().equals(INT_PASSWORD))
                    wcefPassword = wcefField;
                else if (wcefField.getName().equals(INT_PHASE2))
                    wcefPhase2 = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                    wcefPrivateKey = wcefField;
            }


            Method wcefSetValue = null;
            if(!noEnterpriseFieldType){
                for(Method m: wcEnterpriseField.getMethods())
                    //System.out.println(m.getName());
                    if(m.getName().trim().equals("setValue"))
                        wcefSetValue = m;
            }


        /*EAP Method*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefEap.get(selectedConfig), ENTERPRISE_EAP);
            }
            else
            {
                wcefEap.set(selectedConfig, ENTERPRISE_EAP);
            }
        /*EAP Phase 2 Authentication*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefPhase2.get(selectedConfig), ENTERPRISE_PHASE2);
            }
            else
            {
                wcefPhase2.set(selectedConfig, ENTERPRISE_PHASE2);
            }
        /*EAP Anonymous Identity*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefAnonymousId.get(selectedConfig), ENTERPRISE_ANON_IDENT);
            }
            else
            {
                wcefAnonymousId.set(selectedConfig, ENTERPRISE_ANON_IDENT);
            }
        /*EAP CA Certificate*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefCaCert.get(selectedConfig), ENTERPRISE_CA_CERT);
            }
            else
            {
                wcefCaCert.set(selectedConfig, ENTERPRISE_CA_CERT);
            }
        /*EAP Private key*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefPrivateKey.get(selectedConfig), ENTERPRISE_PRIV_KEY);
            }
            else
            {
                wcefPrivateKey.set(selectedConfig, ENTERPRISE_PRIV_KEY);
            }
        /*EAP Identity*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefIdentity.get(selectedConfig), userName);
            }
            else
            {
                wcefIdentity.set(selectedConfig, userName);
            }
        /*EAP Password*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefPassword.get(selectedConfig), passString);
            }
            else
            {
                wcefPassword.set(selectedConfig, passString);
            }
        /*EAp Client certificate*/
            if(!noEnterpriseFieldType)
            {
                wcefSetValue.invoke(wcefClientCert.get(selectedConfig), ENTERPRISE_CLIENT_CERT);
            }
            else
            {
                wcefClientCert.set(selectedConfig, ENTERPRISE_CLIENT_CERT);
            }
            // Adhoc for CM6
            // if non-CM6 fails gracefully thanks to nested try-catch

            try{
                Field wcAdhoc = WifiConfiguration.class.getField("adhocSSID");
                Field wcAdhocFreq = WifiConfiguration.class.getField("frequency");
                //wcAdhoc.setBoolean(selectedConfig, prefs.getBoolean(PREF_ADHOC,
                //      false));
                wcAdhoc.setBoolean(selectedConfig, false);
                int freq = 2462;    // default to channel 11
                //int freq = Integer.parseInt(prefs.getString(PREF_ADHOC_FREQUENCY,
                //"2462"));     // default to channel 11
                //System.err.println(freq);
                wcAdhocFreq.setInt(selectedConfig, freq);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            // FIXME As above, what should I do here?
            e.printStackTrace();
        }

        WifiManager wifiManag = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifiManag.addNetwork(selectedConfig);
        Log.d("WifiPreference", "add Network returned " + res );
        boolean b = wifiManag.enableNetwork(selectedConfig.networkId, false);
        Log.d("WifiPreference", "enableNetwork returned " + b );
        boolean c = wifiManag.saveConfiguration();
        Log.d("WifiPreference", "Save configuration returned " + c );
        boolean d = wifiManag.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + d );
    }





    void saveWepConfig()
    {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"WORK\""; //IMP! This should be in Quotes!!
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        wc.wepKeys[0] = "\"aaabbb1234\""; //This is the WEP Password
        wc.wepTxKeyIndex = 0;

        WifiManager  wifiManag = (WifiManager) this.getSystemService(WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifi.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean es = wifi.saveConfiguration();
        Log.d("WifiPreference", "saveConfiguration returned " + es );
        boolean b = wifi.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
