/*
  * @author STMicroelectronics MMY Application team
  *
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2017 STMicroelectronics</center></h2>
  *
  * Licensed under ST MIX_MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/Mix_MyLiberty  
  *
  * Unless required by applicable law or agreed to in writing, software 
  * distributed under the License is distributed on an "AS IS" BASIS, 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
  * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/

package com.st.st25nfc.generic.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;

public class ContactHelper {

    final static String TAG = "ContactHelper";
    public Uri mUriContact;
    private ContentResolver mContentResolver;
    private String mId;
    private String mVCardVersion = null;
    private Hashtable mBalance = new Hashtable();


    /**
     *
     */
    public ContactHelper() {
        // TODO Auto-generated constructor stub
        mUriContact = null;
        mContentResolver = null;
        mId = null;
        mVCardVersion = null;
    }

    public ContactHelper(Uri uriContact, ContentResolver contentResolver) {
        // TODO Auto-generated constructor stub
        mUriContact = uriContact;
        mContentResolver = contentResolver;
        mId = getContactIdFromAddressBook(mUriContact);
        // used only for test
        //getVCF();
        //mVCardVersion = getVCardVersion(mId);
        getVCFHashtable();
    }

    public String getId() {
        return mId;
    }

    public Bitmap retrieveContactPhoto(String id) {

        Bitmap photo = null;
        Bitmap resized = null;
        int maxHeight = 256;
        int maxWidth = 256;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(mContentResolver,
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                // Resize the Photo to 256 * 256 to fit in a 64k
                if (photo.getHeight() > maxHeight || photo.getWidth() > maxWidth) {
                    // resize to size
                    resized = Bitmap.createScaledBitmap(photo, maxHeight, maxWidth, true);
                } else {
                    resized = photo;
                }

                inputStream.close();
                return resized;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String retrieveContactInfo(Uri uri, String[] projection, String whereName, String[] whereNameParams)
    {
        String ret = null;
        Cursor c = null;
        try {
            c = mContentResolver.query(uri, projection, whereName, whereNameParams, null);
            if (c != null && c.moveToFirst()) {
                int indexDisplayName = c.getColumnIndexOrThrow(projection[0]);
                ret = c.getString(indexDisplayName);
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
        return ret;
    }

    public String getContactIdFromAddressBook(Uri uriContact) {
        String ret = null;
        Uri uri = uriContact;
        String[] projection = new String[]{ContactsContract.Contacts._ID};
        String whereName = null;
        String[] whereNameParams = null;

        ret = retrieveContactInfo    (uri, projection, null, null);
        Log.d(TAG, "getContactIdFromAddressBook :" + ret);

        return ret;
    }

    public String getVCardVersion(String id)
    {
        String ret = null;

        Uri uri = ContactsContract.Data.CONTENT_URI;
//        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredName.DATA_VERSION};
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DATA_VERSION};
        String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.Phone.DATA_VERSION };

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);
        Log.d(TAG, "getVCardVersion :" + ret);

        return ret;
    }

    public String getDisplayName(String id)
    {
        String ret = null;
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME};
        String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);

        return ret;
    }


    public String retrieveContactEmail(String id) {
        String ret = null;
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
        String whereName = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
        String[] whereNameParams = new String[]{id};

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);


        return ret;


    }

    public String retrieveContactWebSite(String id) {
        String ret = null;
/*        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[] {Website.URL};
        String whereName = ContactsContract.Data.CONTACT_ID + " = " + id + " AND ContactsContract.Data.MIMETYPE = '"
                + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                + "'";
        String[] whereNameParams = null;*/

        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Website.URL};
        //String whereName = ContactsContract.Data.CONTACT_ID + " = " + id ;
        String whereName = ContactsContract.Data.CONTACT_ID + " = " + id + " AND ContactsContract.Data.MIMETYPE = '"
                + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                + "'";
        String[] whereNameParams = null;

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);
        if (ret == null) {
            ret = (String)this.mBalance.get("vnd.android.cursor.item/website");
        }

        return ret;


    }


    public String retrieveContactStructurePostAddr(String id) {
        String ret = null;
        Uri uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};
        String whereName = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ? ";
        String[] whereNameParams = new String[]{id};

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);


        return ret;


    }


    public String retrieveContactNumber(String id) {
        String ret = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        String whereName = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        String[] whereNameParams = new String[]{id};

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);


        return ret;


    }

    public HashMap<String, String> getFullName(String id)
    {
        HashMap<String, String> ret = new HashMap<String, String>();

        String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
        Cursor c = null;
        try {
            c = mContentResolver.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, null);
            if (c != null && c.moveToFirst()) {
                int indexGivenName = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                int indexFamilyName = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                int indexDisplayName = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);

                ret.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, c.getString(indexGivenName));
                ret.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, c.getString(indexFamilyName));
                ret.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, c.getString(indexDisplayName));
            }
        }
        catch(Exception e) {
            Log.e("getFullName", e.getMessage());
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
        return ret;
    }


    private  void getVCFHashtable() {
     //String ret = new String();
     Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, Long.parseLong(getId()));
     Uri entityUri = Uri.withAppendedPath(this.mUriContact, android.provider.ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);
     Cursor c = mContentResolver.query(entityUri,
              new String[]{RawContacts.SOURCE_ID, android.provider.ContactsContract.Contacts.Entity.DATA_ID, android.provider.ContactsContract.Contacts.Entity.MIMETYPE, android.provider.ContactsContract.Contacts.Entity.DATA1},
              null, null, null);
     mBalance.clear();
     try {
         while (c.moveToNext()) {
             String sourceId = c.getString(0);
             //ret = ret + sourceId;
             if (!c.isNull(1)) {
                 String mimeType = c.getString(2);
                 //ret = ret + "  " + mimeType;
                 String data = c.getString(3);
                 //ret = ret + "  " + data;
                 if (mimeType != null &&  data != null) mBalance.put(mimeType, data);
             }
             //ret = ret + "\n";
         }
     }
         catch(Exception e) {
                Log.e("getVCFHashtable", e.getMessage());
            }
     finally {
         c.close();
     }
    //return ret;
}

    public  void getVCF() {
        final String vfile = "Contacts.csv";
        Cursor phones = mContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        phones.moveToFirst();

        do {
            String lookupKey = phones.getString(phones
                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            AssetFileDescriptor fd;
            try {
                fd = mContentResolver.openAssetFileDescriptor(uri,
                        "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = new byte[(int) fd.getDeclaredLength()];
                fis.read(buf);
                String VCard = new String(buf);
                String path = Environment.getExternalStorageDirectory()
                        .toString() + File.separator + vfile;
                FileOutputStream mFileOutputStream = new FileOutputStream(path,
                        true);
                mFileOutputStream.write(VCard.toString().getBytes());
                phones.moveToNext();
                Log.d("Vcard", VCard);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } while (phones.moveToNext());

    }

}
