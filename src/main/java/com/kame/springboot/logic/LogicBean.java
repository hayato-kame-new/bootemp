package com.kame.springboot.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

//サービスから、このロジックを呼び出して使う ロジックは、サービス同士で共通の処理をまとめるための場所
	// サービスの中で、リポジトリをフィールドとしてBeanインスタンスをメンバとしているように、
	// このロジックのクラスも、まず、Bean化できるように、Beanクラスとして作り、サービスの中で、@Autowiredを使って、インスタンスを自動生成できるようにしていく。
// このBeanクラスを、構成クラスに登録します。
// これは @Component を付けます

@Component
public class LogicBean {

	public LogicBean() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	 /**
     * データを全て読み込んでbyte配列に格納して返す インスタンスメソッド
     * Beanとして、登録されると、@Autowiredをつけるだけで、フィールドとして宣言すると、
     * 自動で、Beanインスタンスが生成されますので、
     * staticなメソッド(静的メソッド クラスメソッド) じゃなくて、
     * 非staticなメソッドとして、インスタンスメソッドを定義する
     * このメソッドは、サービスクラスで利用する、サービスクラスの インスタンスフィールドとして、メンバに取り込んで利用する @Autowiredをつけると、自動でBeanインスタンスが生成される
     * 
     * @param is
     * @return byte[]
     * @throws IOException
     */
    public byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(); // newで確保
        byte[] buffer = new byte[1024];  // 1024バイト  1キロバイト

        while (true) { // 無限ループ注意
            int len = is.read(buffer); // 1024バイトだけ取り込む
            if (len < 0) { // ループの終わりの条件
                break; // ループ抜ける break  必須  無限ループに注意 
            }
            bout.write(buffer, 0, len);  // 指定しただけoutputstreamにバッファに書き込みする　最後の端数も取り込めるようにする
        }
        return bout.toByteArray(); // 戻り値 byte[] です
        // 取り込んだものをバイト配列にして戻している
    }

    
    // テスト用
    public void logic_test() {
    	System.out.println("Hello LogicBean!!");
    }

}
