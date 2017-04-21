package pt.iscte.daam.contactmanager.model;

/**
 * Created by cserrao on 21/04/2017.
 */

public class Contact {
    protected String _id_contact;
    protected String name_contact;
    protected String telephone_contact;

    public Contact(String _id_contact, String name_contact, String telephone_contact) {
        this._id_contact = _id_contact;
        this.name_contact = name_contact;
        this.telephone_contact = telephone_contact;
    }

    public String get_id_contact() {
        return _id_contact;
    }

    public void set_id_contact(String _id_contact) {
        this._id_contact = _id_contact;
    }

    public String getName_contact() {
        return name_contact;
    }

    public void setName_contact(String name_contact) {
        this.name_contact = name_contact;
    }

    public String getTelephone_contact() {
        return telephone_contact;
    }

    public void setTelephone_contact(String telephone_contact) {
        this.telephone_contact = telephone_contact;
    }
}
