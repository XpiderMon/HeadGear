package me.mejbha.headgear.util;


import me.mejbha.headgear.HeadGear;

public class UpdateAPI {


    public boolean hasGithubUpdate(String owner, String repository) {
        try (java.io.InputStream inputStream = new java.net.URL(
                "https://api.github.com/repos/" + owner + "/" + repository + "/releases/latest").openStream()) {
            org.json.JSONObject response = new org.json.JSONObject(new org.json.JSONTokener(inputStream));
            String currentVersion = HeadGear.getInstance().getDescription().getVersion();
            String latestVersion = response.getString("tag_name");
            return !currentVersion.equals(latestVersion);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public String getGithubVersion(String owner, String repository) {
        try (java.io.InputStream inputStream = new java.net.URL(
                "https://api.github.com/repos/" + owner + "/" + repository + "/releases/latest").openStream()) {
            org.json.JSONObject response = new org.json.JSONObject(new org.json.JSONTokener(inputStream));
            return response.getString("tag_name");
        } catch (Exception exception) {
            exception.printStackTrace();
            return HeadGear.getInstance().getDescription().getVersion();
        }
    }
}

