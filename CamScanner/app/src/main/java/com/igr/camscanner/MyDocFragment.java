package com.igr.camscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.igr.camscanner.com.igr.camscanner.camerawork.ImageCaptureActivity;
import com.igr.camscanner.com.igr.camscanner.pdfcreator.MyHeaderAndFooter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyDocFragment extends Fragment implements View.OnClickListener {

    public MyDocFragment() {
    }

    Button pdfBt;
    FileOutputStream fOut;
    Activity activity;
    ImageButton captureButton;
    ImageButton editButton;
    ImageButton galleryButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_doc, container, false);
        initPallets(rootView);
        return rootView;
    }

    private void initPallets(View rootView) {
        activity = getActivity();
        pdfBt = (Button) rootView.findViewById(R.id.pdfBt);
        captureButton = (ImageButton) rootView.findViewById(R.id.cameraBt);
        editButton = (ImageButton) rootView.findViewById(R.id.editBt);
        galleryButton = (ImageButton) rootView.findViewById(R.id.galleryBt);
        pdfBt.setOnClickListener(this);
        captureButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
    }

    private void createPdf() {
        Document document = new Document(PageSize.A4, 38, 38, 38, 38);

        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "/M_CamScanner");
            if (!dir.exists()) {
                dir.mkdirs();
                Toast.makeText(getActivity(), "dir created", Toast.LENGTH_SHORT).show();
            }

            Log.d("PDFCreator", "PDF Path: " + dir.getAbsolutePath());

            long time = System.currentTimeMillis();
            String fileName = "sample_" + time + ".pdf";
            File file = new File(dir, fileName);
            fOut = new FileOutputStream(file);

            PdfWriter pdfWriter = PdfWriter.getInstance(document, fOut);
            pdfWriter.setPageEvent(new MyHeaderAndFooter());
            //open the document
            document.open();


            Paragraph p1 = new Paragraph("Sample PDF CREATION USING IText");
            Font paraFont = new Font(Font.FontFamily.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            document.add(p1);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.img);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.scaleToFit(595, 815);
            myImg.setAbsolutePosition((PageSize.A4.getWidth() - myImg.getScaledWidth()) / 2, 35);
            //myImg.setAlignment(Image.ALIGN_MIDDLE);
            //add image to document
            // document.add(myImg);
            pdfWriter.getDirectContent().addImage(myImg);

            document.newPage();


            try {
                fOut.flush();


            } catch (IOException ioe) {
            }

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            document.close();
        }

        try {

            fOut.close();

        } catch (IOException ioe) {
        }

    }

    @Override
    public void onClick(View v) {
        if (v == pdfBt) {
            createPdf();
        }
        switch (v.getId()) {
            case R.id.cameraBt:
                Intent iCamera=new Intent(getActivity(), ImageCaptureActivity.class);
                startActivity(iCamera);
                break;
            case R.id.editBt:
                break;
            case R.id.galleryBt:
                break;
            case R.id.pdfBt:
                break;


        }
    }
}
