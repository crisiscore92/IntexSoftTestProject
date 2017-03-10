package com.crisiscore.www.intexsofttestproject;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

class FirebaseStorageManager {

    private StorageReference storageRef;

    FirebaseStorageManager() {

        initialize();

    }

    private void initialize(){

        FirebaseStorage storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference();

    }

    UploadTask uploadImage(Uri file){

        StorageReference reference = storageRef.child("images/"+file.getLastPathSegment());

        UploadTask uploadTask = reference.putFile(file);

        return uploadTask;

    }

    void deleteImage(Uri file){

        StorageReference reference = storageRef.child("images/" + file.getLastPathSegment());

        reference.delete();

    }

    StorageReference downloadImage(Uri file){

        return storageRef.child("images/" + file.getLastPathSegment());

    }

}
