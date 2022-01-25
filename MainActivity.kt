package com.example.electronic_photo_frame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }//등록
    private val startPhotoFrameModeButton: Button by lazy {
        findViewById<Button>(R.id.startPhotoFrameModeButton)
    }//등록
    private val imageUriList:MutableList<Uri> = mutableListOf()

    private val imageViewList: List<ImageView> by lazy{
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageView11))
            add(findViewById(R.id.imageView12))
            add(findViewById(R.id.imageView13))
            add(findViewById(R.id.imageView21))
            add(findViewById(R.id.imageView22))
            add(findViewById(R.id.imageView23))

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }

    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {

            when {
                ContextCompat.checkSelfPermission( // 권한을 수락했는지 아닌지 확인하기위한 것. 인자로는 context , permission  이다
                    this, // 이 context 사용
                    android.Manifest.permission.READ_EXTERNAL_STORAGE// 외부 저장소에서 읽을수있도록 하는 것.
                ) == PackageManager.PERMISSION_GRANTED -> { // 이 허락되었냐?
                    //TODO 권환이 잘 부여되었을때 갤러리에서 사진을 선택하는 기능
                    if (imageUriList.size == 6){
                        Toast.makeText(this,"6개까지만",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }else{
                        navigatePhotos()
                    }
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> { // 권한을 요청하기전 근거가있는 ui를 표시여부. // 이미 한번 거절되었던 상태면
                    // TODO 교육용 팝업 확인 후 권한 팝업을 띄우는기능
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
        }
    }
    private fun initStartPhotoFrameModeButton(){
        startPhotoFrameModeButton.setOnClickListener {
            val intent = Intent(this,PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed{ index, uri ->
                intent.putExtra("photo$index",uri.toString())
            }
            intent.putExtra("photoListSize",imageUriList.size)
            startActivity(intent)

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) { // requestCode 를 전달해주는 함수가 정해저있음. 그게 이 함수다.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // grantResults 의 값은 PackageManger.PERMISSION_GRANTED OR PackageManger.PERMISSION DENIED 이다.

        when (requestCode) {
            1000 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // todo 권한이 부여된것.
                    navigatePhotos()
                } else {
                    Toast.makeText(this, "권한을 거절하였습니다.", Toast.LENGTH_SHORT).show()
                }

            else -> {
                //
            }
        }
    }

    private fun navigatePhotos(){
        val intent = Intent(Intent.ACTION_GET_CONTENT) // action_get_content 는 특정 종류의 데이터를 원할때 사용한다.
        intent.type = "image/*"  // jpg,png인지 타입 형식 확인
        startActivityForResult(intent,2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        when(requestCode){
            2000->{
                val selectedImageUri: Uri? = data?.data
                if(selectedImageUri != null){
//                    if (imageUriList.size == 6){
//                        Toast.makeText(this,"6개까지만",Toast.LENGTH_SHORT).show()
//                        return
//                    } 이미 꽉채워져있으면 사진첩으로 이동하지 않고 경고문 출력하기 위해 add 버튼으로 옮겨감

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)

                }else{
                    Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()

                }
            }
            else->{
                Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup(){
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기"){_, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
            }
            .setNegativeButton("취소하기") {_,_-> }
            .create()
            .show()
    } // alertdialog사용 팝업 화면 제작 함수

}