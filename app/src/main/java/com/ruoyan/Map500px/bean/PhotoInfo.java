package com.ruoyan.map500px.bean;

/**
 * Created by ruoyan on 3/5/15.
 */
public class PhotoInfo {
    private String author;
    private String camera;
    private String lens;
    private String focal;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getLens() {
        return lens;
    }

    public void setLens(String lens) {
        this.lens = lens;
    }

    public String getFocal() {
        return focal;
    }

    public void setFocal(String focal) {
        this.focal = focal;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getShutter() {
        return shutter;
    }

    public void setShutter(String shutter) {
        this.shutter = shutter;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    private String iso;
    private String shutter;
    private String aperture;
    private String id;

    public String getFullSizeUrl() {
        return fullSizeUrl;
    }

    public void setFullSizeUrl(String fullSizeUrl) {
        this.fullSizeUrl = fullSizeUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String fullSizeUrl;

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    private String thumbnailUrl;

    public PhotoInfo(String author, String camera, String lens, String focal, String iso,
                     String shutter, String aperture, String id, String fullSizeUrl,
                     String thumbnailUrl,
                     double latitude, double longitude) {
        setAuthor(author);
        setCamera(camera);
        setLens(lens);
        setFocal(focal);
        setIso(iso);
        setShutter(shutter);
        setAperture(aperture);
        setId(id);
        setFullSizeUrl(fullSizeUrl);
        setThumbnailUrl(thumbnailUrl);
        setLatitude(latitude);
        setLongitude(longitude);
    }
}
