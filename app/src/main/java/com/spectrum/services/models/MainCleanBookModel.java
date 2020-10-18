package com.spectrum.services.models;

import java.util.List;

/**
 * Created by Abins Shaji on 02/03/18.
 */

public class MainCleanBookModel {
    private String no_hours;
    private String no_maids;
    private String type_service;
    private String want_materials;
    private String crew_in;
    private String instruction;
    private List<CleanExtraService.ExtraServiceData>cleanExtraServices ;
    private String type_service_id;

    public MainCleanBookModel(String no_hours, String no_maids, String type_service, String want_materials, String crew_in, String instruction, List<CleanExtraService.ExtraServiceData> cleanExtraServices, String type_service_id) {
        this.no_hours = no_hours;
        this.no_maids = no_maids;
        this.type_service = type_service;
        this.want_materials = want_materials;
        this.crew_in = crew_in;
        this.instruction = instruction;
        this.cleanExtraServices = cleanExtraServices;
        this.type_service_id = type_service_id;
    }

    public String getType_service_id() {
        return type_service_id;
    }

    public void setType_service_id(String type_service_id) {
        this.type_service_id = type_service_id;
    }

    public String getNo_hours() {
        return no_hours;
    }

    public void setNo_hours(String no_hours) {
        this.no_hours = no_hours;
    }

    public String getNo_maids() {
        return no_maids;
    }

    public void setNo_maids(String no_maids) {
        this.no_maids = no_maids;
    }

    public String getType_service() {
        return type_service;
    }

    public void setType_service(String type_service) {
        this.type_service = type_service;
    }

    public String getWant_materials() {
        return want_materials;
    }

    public void setWant_materials(String want_materials) {
        this.want_materials = want_materials;
    }

    public String getCrew_in() {
        return crew_in;
    }

    public void setCrew_in(String crew_in) {
        this.crew_in = crew_in;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public List<CleanExtraService.ExtraServiceData> getCleanExtraServices() {
        return cleanExtraServices;
    }

    public void setCleanExtraServices(List<CleanExtraService.ExtraServiceData> cleanExtraServices) {
        this.cleanExtraServices = cleanExtraServices;
    }
}
