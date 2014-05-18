package eu.ttbox.nfcproxy.ui.cardreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.ttbox.nfcproxy.R;
import eu.ttbox.nfcproxy.service.nfc.NfcReaderBroadcastReceiver;
import eu.ttbox.nfcproxy.service.nfc.NfcReaderCallback;
import eu.ttbox.nfcproxy.service.nfc.reader.LoyaltyCardReader;


public class CardReaderFragment extends Fragment implements LoyaltyCardReader.AccountCallback {

    public static final String TAG = "CardReaderFragment";


    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;


    // Service
    private NfcReaderBroadcastReceiver nfcReceiver;

    public NfcReaderCallback mLoyaltyCardReader;

    // Binding
    private TextView mAccountField;


    // ===========================================================
    // Constructor
    // ===========================================================


    /**
     * Called when sample is created. Displays generic UI with welcome text.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.cardreader_fragment, container, false);
        if (v != null) {
            mAccountField = (TextView) v.findViewById(R.id.card_account_field);
            mAccountField.setText("Waiting...");

            mLoyaltyCardReader = new LoyaltyCardReader(this);

            //

            // Disable Android Beam and register our card reader callback
            //enableReaderMode();
        }

        return v;
    }


    // ===========================================================
    // Life Cycle
    // ===========================================================

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }


    // ===========================================================
    // Dialog Service
    // ===========================================================


    protected void startNfcSettingsActivity() {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }


    // ===========================================================
    // Nfc UnRegister Service
    // ===========================================================


    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        // KitKat : nfc.disableReaderMode(activity);
        disableReaderMode(activity, nfc);

    }

    private void disableReaderMode(Activity activity, NfcAdapter nfc) {
        if (nfc != null) {
            nfc.disableForegroundDispatch(activity);
        }
        if (nfcReceiver != null) {
            activity.unregisterReceiver(nfcReceiver);
        }
    }


    // ===========================================================
    // Nfc Register Service
    // ===========================================================

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            //  KitKat :  nfc.enableReaderMode(activity, mLoyaltyCardReader, READER_FLAGS, null);
            if (!nfc.isEnabled()) {
                startNfcSettingsActivity();
            } else {
                enableReaderMode(activity, nfc, mLoyaltyCardReader);
            }
        }
    }


    private void enableReaderMode(Activity activity, NfcAdapter nfc, NfcReaderCallback cardReaderCallback) {
        String[][] nfctechfilter = new String[][]{new String[]{IsoDep.class.getName()}};
        // Broadcast
        nfcReceiver = new NfcReaderBroadcastReceiver(cardReaderCallback);
        IntentFilter nfcReceiverFilter = new IntentFilter();
        nfcReceiverFilter.addAction(NfcReaderBroadcastReceiver.ACTION_ON_NFC_RECEIVE);
        activity.registerReceiver(nfcReceiver, nfcReceiverFilter);
        // Nfc Receiver
        Intent intent = new Intent()
                .setAction(NfcReaderBroadcastReceiver.ACTION_ON_NFC_RECEIVE)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent nfcintent = PendingIntent.getBroadcast(activity, 0, intent, 0);
        // Enable
        nfc.enableForegroundDispatch(activity, nfcintent, null, nfctechfilter);
    }


    // ===========================================================
    // Service Callback
    // ===========================================================

    @Override
    public void onAccountReceived(final String account) {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAccountField.setText(account);
            }
        });
    }


    // ===========================================================
    // Other
    // ===========================================================

}
