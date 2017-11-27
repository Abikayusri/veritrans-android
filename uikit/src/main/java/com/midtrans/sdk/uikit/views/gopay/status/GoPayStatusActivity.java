package com.midtrans.sdk.uikit.views.gopay.status;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.uikit.R;
import com.midtrans.sdk.uikit.abstracts.BasePaymentActivity;
import com.midtrans.sdk.uikit.utilities.SdkUIFlowUtil;
import com.midtrans.sdk.uikit.widgets.DefaultTextView;
import com.midtrans.sdk.uikit.widgets.FancyButton;
import com.midtrans.sdk.uikit.widgets.SemiBoldTextView;

/**
 * Created by Fajar on 16/11/17.
 */

public class GoPayStatusActivity extends BasePaymentActivity {
    public static final String EXTRA_PAYMENT_STATUS = "extra.status";

    private FancyButton buttonPrimary;
    private SemiBoldTextView textTitle;
    private DefaultTextView instructionToggle;

    private boolean isTablet, isInstructionShown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTablet = SdkUIFlowUtil.getDeviceType(this).equals("TABLET");
        setContentView(R.layout.activity_gopay_status);
        initLayout();
        bindData();
    }

    private void initLayout() {
        ViewStub stub = (ViewStub) findViewById(R.id.gopay_layout_stub);
        stub.setLayoutResource(isTablet ? R.layout.layout_gopay_status_tablet : R.layout.layout_gopay_status);
        stub.inflate();
    }

    private void bindData() {
        final TransactionResponse response = (TransactionResponse) getIntent().getSerializableExtra(EXTRA_PAYMENT_STATUS);
        if (response != null) {
            if (isTablet) {
                showProgressLayout();
                //hide confirm button and adjust item details to bottom of screen
                findViewById(R.id.layout_primary_button).setVisibility(View.GONE);
                findViewById(R.id.primary_button_separator).setVisibility(View.GONE);
                View itemDetail = findViewById(R.id.container_item_details);
                RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                itemDetail.setLayoutParams(layoutParams);

                final LinearLayout instructionLayout = (LinearLayout) findViewById(R.id.gopay_instruction_layout);
                instructionToggle = (DefaultTextView) findViewById(R.id.gopay_instruction_toggle);
                instructionToggle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isInstructionShown = !isInstructionShown;
                        if (isInstructionShown) {
                            instructionToggle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up, 0);
                            instructionLayout.setVisibility(View.VISIBLE);
                        } else {
                            instructionToggle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down, 0);
                            instructionLayout.setVisibility(View.GONE);
                        }
                    }
                });

                //process qr code
                final String qrCodeUrl = response.getQrCodeUrl();
                final ImageView qrImage = (ImageView) findViewById(R.id.gopay_qr_code);
                qrImage.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressLayout();
                        loadQrCode(qrCodeUrl, qrImage);
                    }
                });
                loadQrCode(qrCodeUrl, qrImage);
            } else {
                //process deeplink
                buttonPrimary.setText(getString(R.string.payment_method_description_gopay));
                buttonPrimary.setTextBold();
                buttonPrimary.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(GoPayStatusActivity.this, getString(R.string.redirecting_to_gopay), Toast.LENGTH_SHORT)
                            .show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getDeeplinkUrl()));
                        startActivity(intent);
                    }
                });
            }
        }
        textTitle.setText(getString(R.string.payment_method_description_gopay));
    }

    @Override
    public void bindViews() {
        textTitle = (SemiBoldTextView) findViewById(R.id.text_page_title);
        buttonPrimary = (FancyButton) findViewById(R.id.button_primary);
    }

    @Override
    public void initTheme() {
        setPrimaryBackgroundColor(buttonPrimary);
    }

    /**
     * A method for loading QR code based on url that is part of GO-PAY payment response
     * We use Glide listener in order to detect whether image downloading is good or not
     * If it is good, then display the QR code; else display reload icon so this method is
     * called once again.
     * @param url location of QR code to be downloaded
     * @param container view to display the image
     */
    private void loadQrCode(String url, final ImageView container) {
        Glide.with(this)
            .load(url)
            .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    hideProgressLayout();
                    container.setClickable(true);
                    container.setImageResource(R.drawable.ic_refresh);
                    int padding = (int) (80f * getResources().getDisplayMetrics().density);
                    container.setPadding(padding, padding, padding, padding);
                    Toast.makeText(GoPayStatusActivity.this, getString(R.string.error_qr_code), Toast.LENGTH_SHORT)
                        .show();
                    return true;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideProgressLayout();
                    container.setClickable(false);
                    int padding = (int) (16f * getResources().getDisplayMetrics().density);
                    container.setPadding(padding, padding, padding, padding);
                    return false;
                }
            })
            .into(container);
    }
}
