/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;

import com.channelize.apisdk.model.Module;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListModuleResponse implements GenericResponse {

    @SerializedName("response")
    private List<Module> response;

    public List<Module> getModules() {
        return response;
    }

    public void setModules(List<Module> modules) {
        this.response = modules;
    }

}
