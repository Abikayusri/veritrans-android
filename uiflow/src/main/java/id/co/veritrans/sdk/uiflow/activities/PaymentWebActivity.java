package id.co.veritrans.sdk.uiflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import id.co.veritrans.sdk.coreflow.core.Constants;
import id.co.veritrans.sdk.coreflow.core.VeritransSDK;
import id.co.veritrans.sdk.uiflow.R;
import id.co.veritrans.sdk.uiflow.fragments.WebviewFragment;
import id.co.veritrans.sdk.uiflow.widgets.VeritransDialog;

public class PaymentWebActivity extends BaseActivity {
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private VeritransSDK veritransSDK;
    private RelativeLayout webviewContainer;
    private String webUrl;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web);
        saveCurrentFragment = true;
        initializeTheme();
        webUrl = getIntent().getStringExtra(Constants.WEBURL);
        if (getIntent().getStringExtra(Constants.TYPE) != null && !getIntent().getStringExtra(Constants.TYPE).equals("")) {
            type = getIntent().getStringExtra(Constants.TYPE);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        fragmentManager = getSupportFragmentManager();
        toolbar.setTitle(R.string.processing_payment);

        setSupportActionBar(toolbar);
        WebviewFragment webviewFragment;
        if (!type.equals("")) {
            webviewFragment = WebviewFragment.newInstance(webUrl, type);
        } else {
            webviewFragment = WebviewFragment.newInstance(webUrl);
        }
        replaceFragment(webviewFragment, R.id.webview_container, true, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_close) {
            VeritransDialog veritransDialog = new VeritransDialog(this, getString(R.string.cancel_transaction),
                    getString(R.string.cancel_transaction_message), getString(android.R.string.yes), getString(android.R.string.no));
            veritransDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                }
            });
            veritransDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        VeritransDialog veritransDialog = new VeritransDialog(this,getString(R.string.cancel_transaction),
                getString(R.string.cancel_transaction_message),getString(android.R.string.yes),getString(android.R.string.no));
        veritransDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        veritransDialog.show();

    }

}
