package com.hermes.chm.integraciones.marketplace.cencosud.connector.models;

//{
//     "id": "5922ce45-af17-11ec-a1c9-0e8f869ec6f7",
//     "name": "everyone ADMIN can GET at ANY is ALLOW",
//     "action": "GET",
//     "effect": "ALLOW",
//     "target": "ANY",
//     "resource": "SELLERS",
//     "abilities": []
//     }
public class CSPolicy {
    private String id;
    private String name;
    private String action;
    private String effect;
    private String target;
    private String resource;
    private String[] abilities;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String[] getAbilities() {
        return abilities;
    }

    public void setAbilities(String[] abilities) {
        this.abilities = abilities;
    }

}
