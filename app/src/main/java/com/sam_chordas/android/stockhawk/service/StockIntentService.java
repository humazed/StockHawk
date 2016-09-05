package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")) {
            args.putString("symbol", intent.getStringExtra("symbol"));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.

        Handler mHandler = new Handler(getMainLooper());
        int result = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
        if (result == GcmNetworkManager.RESULT_FAILURE) {
            mHandler.post(() -> Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.error_symbol_doesnt), Toast.LENGTH_LONG).show());
        } else if (result == StockTaskService.RESULT_ADD_SUCCESS) {
            mHandler.post(() -> Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.added_success), Toast.LENGTH_LONG).show());
        }

    }
}
