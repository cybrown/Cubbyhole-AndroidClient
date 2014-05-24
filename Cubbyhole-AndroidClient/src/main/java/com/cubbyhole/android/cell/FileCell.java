package com.cubbyhole.android.cell;

import com.cubbyhole.client.model.File;

public class FileCell {
    private File file;
    private boolean checked;

    public FileCell(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
