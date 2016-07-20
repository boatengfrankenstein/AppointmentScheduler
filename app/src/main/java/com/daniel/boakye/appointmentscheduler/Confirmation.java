package com.daniel.boakye.appointmentscheduler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Confirmation extends Activity implements View.OnClickListener {

    Date dateConfirm = new Date();
    String doctor;
    SimpleDateFormat sfDate = new SimpleDateFormat("E, MMMM  d  yyyy");
    SimpleDateFormat sfTime = new SimpleDateFormat("h:mm a");
    Button button;
    EditText emailText;

    Session session = null;
    ProgressDialog pdialog = null;
    Context context = null;
    EditText reciep, sub, msg;
    String rec, subject, textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        Intent intent = getIntent();
        doctor = intent.getStringExtra("doctor");
        dateConfirm.setTime(intent.getLongExtra("date", -1));

        setDocDayTimeText();

        button = (Button) findViewById(R.id.confirmButton);
        emailText = (EditText) findViewById(R.id.emailText);
        String email = emailText.getText().toString();
        button.setOnClickListener(this);

    }

    private void setDocDayTimeText() {
        TextView doctorTextConfirm = (TextView) findViewById(R.id.doctorTextConfirm);
        TextView dateTextConfirm = (TextView) findViewById(R.id.dateTextConfirm);
        TextView timeTextConfirm = (TextView) findViewById(R.id.timeTextConfirm);

        doctorTextConfirm.setText(doctor);
        dateTextConfirm.setText(sfDate.format(dateConfirm));
        timeTextConfirm.setText(sfTime.format(dateConfirm));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("iominati.io@gmail.com", "DANb111666");
            }
        });
  String subjectofemail =  "A new appointment has been made for patient on " + sfDate.format(dateConfirm)
          + " at time " + sfTime.format(dateConfirm) + ". Please be sure to " +
          "arrive 15 minutes before the scheduled appointment time to go through " +
          "necessary paperwork.";

        pdialog = ProgressDialog.show(context, "", "Sending Mail...", true);
       String usermail[]={ emailText.getText().toString(),subjectofemail};
        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute(usermail);
    }


    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(params[0]));
                Log.i("usercparam", params[0]);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("iominati.io@gmail.com"));
                message.setSubject("Appointment for Patient");
                message.setContent(params[1], "text/html; charset=utf-8");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pdialog.dismiss();
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
        }
    }
}