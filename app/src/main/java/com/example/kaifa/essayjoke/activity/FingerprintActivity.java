package com.example.kaifa.essayjoke.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.kaifa.essayjoke.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * 指纹识别
 */
public class FingerprintActivity extends AppCompatActivity {

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private static final String DEFAULT_KEY_NAME = "default_key";
    private Cipher defaultCipher;

    private FingerprintManagerCompat fingerprintManager;
    private KeyguardManager keyguardManager;

//    //非对称加密
//    private KeyPairGenerator mKeyPairGenerator;
//    private Signature mSignature;

    public static final String TAG = FingerprintActivity.class.getSimpleName();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(FingerprintActivity.this, "msg:" + msg.obj, Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        //KeyStore 是用于存储、获取密钥（Key）的容器
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        //对称加密生成 Key，需要 KeyGenerator 类
        //获取一个 KeyGenerator 对象
        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }

        //获得 KeyGenerator 对象后，生成一个 Key
        createKey(DEFAULT_KEY_NAME, true);

        //创建并初始化 Cipher 对象
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        //使用刚才创建好的密钥，初始化 Cipher 对象:
        initCipher(defaultCipher, DEFAULT_KEY_NAME);

//=========================================================================
//========================================================================

       /*
        // 非对称加密，创建 KeyPairGenerator 对象
        try {
            mKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyPairGenerator", e);
        }
        //得到了 KeyPairGenerator 对象后，创建 KeyPair（密钥对）了：
        try {
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                            KeyProperties.PURPOSE_SIGN)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                            // Require the user to authenticate with a fingerprint to authorize
                            // every use of the private key
                            .setUserAuthenticationRequired(true)
                            .build());
            mKeyPairGenerator.generateKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        // 使用私钥签名
        try {
            mSignature = Signature.getInstance("SHA256withECDSA");
            mKeyStore.load(null);
            PrivateKey key = (PrivateKey) mKeyStore.getKey(DEFAULT_KEY_NAME, null);
            mSignature.initSign(key);
        } catch (KeyPermanentlyInvalidatedException e) {
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(mSignature);
        */

//=========================================================================
//========================================================================

        //获得FingerprintManager对象引用
        fingerprintManager = FingerprintManagerCompat.from(this);
        //检查系统当中是不是有指纹识别的硬件
        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(this, "not find hardware", Toast.LENGTH_SHORT).show();
            return;
        }

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        //检查当前设备是不是处于安全保护中的
        if (keyguardManager.isKeyguardSecure()) {
            //检查当前系统中是不是已经有注册的指纹信息
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                Toast.makeText(this, "need set a fingger", Toast.LENGTH_SHORT).show();
                return;
            }

            /**
             * 扫描用户按下的指纹
             *
             * Request authentication of a crypto object. This call warms up the fingerprint hardware
             * and starts scanning for a fingerprint. It terminates when
             * {@link FingerprintManagerCompat.AuthenticationCallback#onAuthenticationError(int, CharSequence)} or
             * {@link FingerprintManagerCompat.AuthenticationCallback#onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult)} is called, at
             * which point the object is no longer valid. The operation can be canceled by using the
             * provided cancel object.
             *
             * @param crypto object associated with the call or null if none required.
             *               crypto这是一个加密类的对象，指纹扫描器会使用这个对象来判断认证结果的合法性。这个对象可以是null，
             *               但是这样的话，就意味这app无条件信任认证的结果，虽然从理论上这个过程可能被攻击，数据可以被篡改，
             *               这是app在这种情况下必须承担的风险
             *               。因此，建议这个参数不要置为null。这个类的实例化有点麻烦，
             *               主要使用javax的security接口实现
             * @param flags optional flags; should be 0
             * @param cancel an object that can be used to cancel authentication
             *               cancel 这个是CancellationSignal类的一个对象，这个对象用来在指纹识别器扫描
             *               用户指纹的是时候取消当前的扫描操作，如果不取消的话，那么指纹扫描器会移植扫描直到超时
             *               （一般为30s，取决于具体的厂商实现），这样的话就会比较耗电。建议这个参数不要置为null
             * @param callback an object to receive authentication events
             *                 这个是FingerprintManager.AuthenticationCallback类的对象，这个是这个接口
             *                 中除了第一个参数之外最重要的参数了。当系统完成了指纹认证过程（失败或者成功都会）后，
             *                 会回调这个对象中的接口，通知app认证的结果。这个参数不能为NULL。
             * @param handler an optional handler for events
             *                这是Handler类的对象，如果这个参数不为null的话，那么FingerprintManager将会使用这个
             *                handler中的looper来处理来自指纹识别硬件的消息。通常来讲，开发这不用提供这个参数，
             *                可以直接置为null，因为FingerprintManager会默认使用app的main looper来处理
             */
            fingerprintManager.authenticate(new FingerprintManagerCompat.CryptoObject(defaultCipher),
                    0, cancellationSignal, callback, null);
            Toast.makeText(this, "fdsfdsafdsafd", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "需要安全保护", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 取消指纹扫描
     * <p>
     * 上面我们提到了取消指纹扫描的操作，这个操作是很常见的。这个时候可以使用CancellationSignal这个类的cancel方法实现
     */
    CancellationSignal cancellationSignal = new CancellationSignal();

    private int error = 0;
    private int failed = 1;
    private int help = 2;
    private int success = 3;
    FingerprintManagerCompat.AuthenticationCallback callback = new FingerprintManagerCompat.AuthenticationCallback() {
        //这个接口会再系统指纹认证出现不可恢复的错误的时候才会调用，并且参数errorCode就给出了错误码，
        //标识了错误的原因。这个时候app能做的只能是提示用户重新尝试一遍
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            Message message = Message.obtain();
            message.what = error;
            message.obj = errString.toString();
            handler.sendMessage(message);
        }

        //这个接口会在系统指纹认证失败的情况的下才会回调。注意这里的认证失败和上面的认证错误是不一样的，
        //虽然结果都是不能认证。认证失败是指所有的信息都采集完整，并且没有任何异常，
        //但是这个指纹和之前注册的指纹是不相符的；但是认证错误是指在采集或者认证的过程中出现了错误，
        //比如指纹传感器工作异常等。也就是说认证失败是一个可以预期的正常情况，而认证错误是不可预期的异常情况
        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Message message = Message.obtain();
            message.what = failed;
            message.obj = "失败";
            handler.sendMessage(message);
        }

        //上面的认证失败是认证过程中的一个异常情况，我们说那种情况是因为出现了不可恢复的错误，
        // 而我们这里的OnAuthenticationHelp方法是出现了可以回复的异常才会调用的。
        // 什么是可以恢复的异常呢？一个常见的例子就是：手指移动太快，当我们把手指放到传感器上的时候，
        // 如果我们很快地将手指移走的话，那么指纹传感器可能只采集了部分的信息，因此认证会失败。但是这个错误是可以恢复的，
        // 因此只要提示用户再次按下指纹，并且不要太快移走就可以解决。
        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
            Message message = Message.obtain();
            message.what = help;
            message.obj = "helpMsgId:" + helpMsgId + "\n\rhelpString" + helpString.toString();
            handler.sendMessage(message);
        }

        //这个接口会在认证成功之后回调。我们可以在这个方法中提示用户认证成功。这里需要说明一下，
        // 如果我们上面在调用authenticate的时候，我们的CryptoObject不是null的话，
        // 那么我们在这个方法中可以通过AuthenticationResult来获得Cypher对象然后调用它的doFinal方法。
        // doFinal方法会检查结果是不是会拦截或者篡改过，如果是的话会抛出一个异常。
        // 当我们发现这些异常的时候都应该将认证当做是失败来来处理，为了安全建议大家都这么做
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            try {
                result.getCryptoObject().getCipher().doFinal();
                Message message = Message.obtain();
                message.what = success;
                message.obj = "成功";
                handler.sendMessage(message);

                Field field = result.getClass().getDeclaredField("mFingerprint");
                field.setAccessible(true);
                Object fingerPrint = field.get(result);

                Class<?> clzz = Class.forName("android.hardware.fingerprint.Fingerprint");
                Method getName = clzz.getDeclaredMethod("getName");
                Method getFingerId = clzz.getDeclaredMethod("getFingerId");
                Method getGroupId = clzz.getDeclaredMethod("getGroupId");
                Method getDeviceId = clzz.getDeclaredMethod("getDeviceId");

                CharSequence name = (CharSequence) getName.invoke(fingerPrint);
                int fingerId = (int) getFingerId.invoke(fingerPrint);
                int groupId = (int) getGroupId.invoke(fingerPrint);
                long deviceId = (long) getDeviceId.invoke(fingerPrint);

                Log.d(TAG, "name: " + name);
                Log.d(TAG, "fingerId: " + fingerId);
                Log.d(TAG, "groupId: " + groupId);
                Log.d(TAG, "deviceId: " + deviceId);
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }


    };

    /**
     * 获得 KeyGenerator 对象后，生成一个 Key
     *
     * @param keyName
     * @param invalidatedByBiometricEnrollment
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        try {
            mKeyStore.load(null);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化 Cipher 对象
     *
     * @param cipher
     * @param keyName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

}
