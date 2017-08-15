package com.midtrans.sdk.uikit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.midtrans.sdk.corekit.core.PaymentType;
import com.midtrans.sdk.uikit.R;
import com.midtrans.sdk.uikit.fragments.InstructionATMBersamaFragment;
import com.midtrans.sdk.uikit.fragments.InstructionAltoFragment;
import com.midtrans.sdk.uikit.fragments.InstructionAtmBniFragment;
import com.midtrans.sdk.uikit.fragments.InstructionBCAFragment;
import com.midtrans.sdk.uikit.fragments.InstructionBCAKlikFragment;
import com.midtrans.sdk.uikit.fragments.InstructionBCAMobileFragment;
import com.midtrans.sdk.uikit.fragments.InstructionBniInternetFragment;
import com.midtrans.sdk.uikit.fragments.InstructionBniMobileFragment;
import com.midtrans.sdk.uikit.fragments.InstructionMandiriFragment;
import com.midtrans.sdk.uikit.fragments.InstructionMandiriInternetFragment;
import com.midtrans.sdk.uikit.fragments.InstructionPermataFragment;
import com.midtrans.sdk.uikit.fragments.InstructionPrimaFragment;
import com.midtrans.sdk.uikit.widgets.MagicViewPager;

/**
 * Created by ziahaqi on 8/15/17.
 */

public class InstructionPagerAdapter extends FragmentStatePagerAdapter {
    private final Context context;
    private final int pageNumber;
    private final String paymentType;
    private int currentPosition = -1;

    public InstructionPagerAdapter(Context context, @NonNull String paymentType, FragmentManager fragmentManager, int pageNumber) {
        super(fragmentManager);
        this.context = context;
        this.pageNumber = pageNumber;
        this.paymentType = paymentType;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (paymentType) {

            case PaymentType.BCA_VA:
                if (position == 0) {
                    fragment = new InstructionBCAFragment();
                } else if (position == 1) {
                    fragment = new InstructionBCAKlikFragment();
                } else {
                    fragment = new InstructionBCAMobileFragment();
                }

                break;

            case PaymentType.PERMATA_VA:
                if (position == 0) {
                    fragment = new InstructionPermataFragment();
                } else {
                    fragment = new InstructionAltoFragment();
                }
                break;

            case PaymentType.E_CHANNEL:
                if (position == 0) {
                    fragment = new InstructionMandiriFragment();

                } else {
                    fragment = new InstructionMandiriInternetFragment();
                }
                break;

            case PaymentType.BNI_VA:
                if (position == 0) {
                    fragment = new InstructionAtmBniFragment();

                } else if (position == 1) {
                    fragment = new InstructionBniMobileFragment();

                } else {
                    fragment = new InstructionBniInternetFragment();
                }

                break;

            default:
                if (position == 0) {
                    fragment = new InstructionATMBersamaFragment();
                } else if (position == 1) {
                    fragment = new InstructionPrimaFragment();
                } else {
                    fragment = new InstructionAltoFragment();
                }

                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return pageNumber;
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        String pageTitle;

        switch (paymentType) {

            case PaymentType.BCA_VA:
                if (position == 0) {
                    pageTitle = context.getString(R.string.tab_bca_atm);
                } else if (position == 1) {
                    pageTitle = context.getString(R.string.tab_bca_klik);
                } else {
                    pageTitle = context.getString(R.string.tab_bca_mobile);
                }

                break;

            case PaymentType.PERMATA_VA:
                if (position == 0) {
                    pageTitle = context.getString(R.string.tab_permata_atm);
                } else {
                    pageTitle = context.getString(R.string.tab_alto);
                }
                break;

            case PaymentType.E_CHANNEL:
                if (position == 0) {
                    pageTitle = context.getString(R.string.tab_mandiri_atm);

                } else {
                    pageTitle = context.getString(R.string.tab_mandiri_internet);
                }
                break;

            case PaymentType.BNI_VA:
                if (position == 0) {
                    pageTitle = context.getString(R.string.tab_atm_bni);
                } else if (position == 1) {
                    pageTitle = context.getString(R.string.tab_bni_mobile);
                } else {
                    pageTitle = context.getString(R.string.tab_bni_internet);
                }

                break;

            default:
                if (position == 0) {
                    pageTitle = context.getString(R.string.tab_atm_bersama);
                } else if (position == 1) {
                    pageTitle = context.getString(R.string.tab_prima);
                } else {
                    pageTitle = context.getString(R.string.tab_alto);
                }

                break;
        }


        return pageTitle;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != currentPosition) {
            Fragment fragment = (Fragment) object;
            MagicViewPager pager = (MagicViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                currentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }
}
