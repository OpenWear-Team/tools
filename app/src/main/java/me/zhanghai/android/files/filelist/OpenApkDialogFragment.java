/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import me.zhanghai.android.files.R;
import me.zhanghai.android.files.filesystem.File;
import me.zhanghai.android.files.util.FragmentUtils;

public class OpenApkDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_PREFIX = OpenApkDialogFragment.class.getName() + '.';

    private static final String EXTRA_FILE = KEY_PREFIX + "FILE";

    @NonNull
    private File mExtraFile;

    @NonNull
    private static OpenApkDialogFragment newInstance(@NonNull File file) {
        //noinspection deprecation
        OpenApkDialogFragment fragment = new OpenApkDialogFragment();
        FragmentUtils.getArgumentsBuilder(fragment)
                .putParcelable(EXTRA_FILE, file);
        return fragment;
    }

    public static void show(@NonNull File file, @NonNull Fragment fragment) {
        OpenApkDialogFragment.newInstance(file)
                .show(fragment.getChildFragmentManager(), null);
    }

    /**
     * @deprecated Use {@link #newInstance(File)} instead.
     */
    public OpenApkDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExtraFile = getArguments().getParcelable(EXTRA_FILE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext(), getTheme())
                .setMessage(R.string.file_open_apk_message)
                .setPositiveButton(R.string.install, (dialog, which) -> getListener().installApk(
                        mExtraFile))
                // While semantically incorrect, this places the two most expected actions side by
                // side.
                .setNegativeButton(R.string.view, (dialog, which) -> getListener().viewApk(
                        mExtraFile))
                .setNeutralButton(android.R.string.cancel, null)
                .create();
    }

    @NonNull
    private Listener getListener() {
        return (Listener) getParentFragment();
    }

    public interface Listener {
        void installApk(@NonNull File file);
        void viewApk(@NonNull File file);
    }
}