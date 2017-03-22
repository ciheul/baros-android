package com.example.ciheul.baros;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ciheul.baros.Fragments.PersonnelFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static android.R.layout.simple_spinner_item;

/**
 * Created by ciheul on 17/03/17.
 */

public class AddNewPersonnel extends AppCompatActivity {

    HashMap<Integer,String> rankMap = new HashMap<Integer, String>();
    HashMap<Integer,String> positionMap = new HashMap<Integer, String>();

    // Edit view object
    EditText namaET;
    EditText nrpET;
    EditText teleponET;
    EditText deskripsiET;

    String investigator = "";
    String encImage = "";
    String picturePath = "";
    String filename = "";

    private RadioButton radio;
    private RadioButton radio1;
    private RadioGroup radioGroup;

    private ImageView imageView;

    private static int RESULT_LOAD_IMAGE = 1;

    // Process Dialog Object
    ProgressDialog prgDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_personnel);

        // Set edit text
        namaET = (EditText)findViewById(R.id.add_personnel_nama);
        nrpET = (EditText)findViewById(R.id.add_personnel_nrp);
        teleponET = (EditText)findViewById(R.id.add_personnel_telepon);
        deskripsiET = (EditText)findViewById(R.id.add_personnel_deskripsi);

        // Set default is_investigator
        radio1 = (RadioButton) findViewById(R.id.radio_inv_true);
        radio1.setChecked(true);

        // Show rank and position
        getRankPosition();

        // Upload personnel ava
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView = (ImageView) findViewById(R.id.personnel_ava);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 10, baos);
            byte[] b = baos.toByteArray();
            encImage = Base64.encodeToString(b, Base64.DEFAULT);

//            try {
//                InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                String encodedImage = encodeImage(selectedImage);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,10,baos);
        byte[] b = baos.toByteArray();
        encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public void getRankPosition() {
        // find id based on progress
        ArrayList<Hasil> hasils = new ArrayList<Hasil>();
        // populate progress spinner(dropdown)
        final ArrayList<String> rankName = new ArrayList<String>();
        // populate penyidik spinner(dropdown)
        final ArrayList<String> positionName = new ArrayList<String>();

        RestClient.get("group/rank/", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);

                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray ranks = obj.getJSONArray("ranks");
                    JSONArray positions = obj.getJSONArray("positions");


                    for (int i = 0; i < ranks.length(); i++) {
                        Hasil hasil = new Hasil();
                        hasil.setRankId((Integer) ranks.getJSONArray(i).get(0));
                        hasil.setRankName((String) ranks.getJSONArray(i).get(1));

                        rankMap.put(hasil.getRankId(), hasil.getRankName());
                        rankName.add(hasil.getRankName());
                    }

                    for (int i = 0; i < positions.length(); i++) {
                        Hasil hasil = new Hasil();
                        hasil.setPositionId((Integer) positions.getJSONArray(i).get(0));
                        hasil.setPositionName((String) positions.getJSONArray(i).get(1));

                        positionMap.put(hasil.getPositionId(), hasil.getPositionName());
                        positionName.add(hasil.getPositionName());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // spinner rank
                Spinner rankSpinner = (Spinner) findViewById(R.id.dropdown_personnel_pangkat);
                rankSpinner.setAdapter(new ArrayAdapter<String>(AddNewPersonnel.this, simple_spinner_item, rankName));

                // spinner penyidik
                Spinner positionSpinner = (Spinner) findViewById(R.id.dropdown_personnel_jabatan);
                positionSpinner.setAdapter(new ArrayAdapter<String>(AddNewPersonnel.this, simple_spinner_item, positionName));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_inv_true:
                if (checked)
                    investigator = "True";
                    break;
            case R.id.radio_inv_false:
                if (checked)
                    investigator = "False";
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_personnel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.save_new_personnel) {
            // Instantiate progress dialog object
            prgDialog = new ProgressDialog(this);
            // Set progress dialog text
            prgDialog.setMessage("Please wait ...");
            // Set cancelable as false
            prgDialog.setCancelable(false);

            // get all data
            String nama = namaET.getText().toString();
            String nrp = nrpET.getText().toString();
            String telepon = teleponET.getText().toString();
            String deskripsi = deskripsiET.getText().toString();

            Spinner rankSpinner = (Spinner) findViewById(R.id.dropdown_personnel_pangkat);
            String rankText = rankSpinner.getSelectedItem().toString();
            String rankVal = null;

            // get rank id
            for (Map.Entry entry:rankMap.entrySet()) {
                if (rankText.equals(entry.getValue())) {
                    rankVal = (String) entry.getValue();
                    break;
                }
            }

            Spinner posSpinner = (Spinner) findViewById(R.id.dropdown_personnel_jabatan);
            String posText = posSpinner.getSelectedItem().toString();
            String posVal = null;

            // get penyidik id
            for (Map.Entry entry:positionMap.entrySet()) {
                if (posText.equals(entry.getValue())) {
                    posVal = (String) entry.getValue();
                    break;
                }
            }

            // check required params
            // nama, nrp
            if(TextUtils.isEmpty(nama)) {
                namaET.setError("Wajib diisi");
            }

            if(TextUtils.isEmpty(nrp)) {
                nrpET.setError("Wajib diisi");
            }

            // is_investigator
            investigator = "True";
            radioGroup = (RadioGroup) findViewById(R.id.radio_inv);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radio_inv_true:
                            investigator = "True";
                            break;

                        case R.id.radio_inv_false:
                            investigator = "False";
                            break;
                    }
                }
            });

            RequestParams params = new RequestParams();

            if (encImage.length() != 0) {
                encImage = "data:image/png;base64," + encImage;
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                params.put("image", encImage);
                params.put("filename", filename);
                params.put("filetype", "png");
            }

            params.put("name", nama);
            params.put("nrp", nrp);
            params.put("rank", rankVal);
            params.put("position", posVal);
            params.put("phone", telepon);
            params.put("description", deskripsi);
            params.put("is_investigator", investigator);

            saveNewPersonnel(params);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void saveNewPersonnel(RequestParams params) {
        // Show progress dialog
        prgDialog.show();

        RestClient.post("personnel/create/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();

                String s = new String(responseBody);

                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    String response = obj.getString("success");
                    finish();
//                    personnelList();
                    Toast.makeText(getApplicationContext(),"Anggota berhasil ditambah.", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

/*    public void personnelList() {
        PersonnelFragment mProfileFragment = new PersonnelFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.addToBackStack(AddNewPersonnel.class
                .getName());
        fragmentTransaction.commit();
    }*/

    // temp spinner information
    public class Hasil {
        private int rankId;
        private String rankName;
        private int positionId;
        private String positionName;

        public int getRankId() {
            return rankId;
        }

        public void setRankId(int rankId) {
            this.rankId = rankId;
        }

        public String getRankName() {
            return rankName;
        }

        public void setRankName(String rankName) {
            this.rankName = rankName;
        }

        public int getPositionId() {
            return positionId;
        }

        public void setPositionId(int positionId) {
            this.positionId = positionId;
        }

        public String getPositionName() {
            return positionName;
        }

        public void setPositionName(String positionName) {
            this.positionName = positionName;
        }

    }

}
