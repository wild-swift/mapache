/*
 * Copyright (C) 2018 Wild Swift
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.wildswift.testapp;

import android.app.Activity;
import android.os.Bundle;

import java.util.Arrays;

import name.wildswift.android.kannotations.interfaces.ObservableListAdapter;
import name.wildswift.testapp.views.CryptoCardViewModel;
import name.wildswift.testapp.views.WalletsView;
import name.wildswift.testapp.views.WalletsViewModel;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WalletsView cardView = findViewById(R.id.cryptoCardView);
        cardView.setViewModel(new WalletsViewModel(
                1537.46f,
                new ObservableListAdapter<>(
                        Arrays.asList(
                                new CryptoCardViewModel(
                                        0.1195656f,
                                        729.5f,
                                        "ZTC",
                                        "Zitcoin",
                                        R.drawable.ic_ztc_icon,
                                        0xFFFF7141
                                ),
                                new CryptoCardViewModel(
                                        2.1195632f,
                                        807.96f,
                                        "ATH",
                                        "Atherium",
                                        R.drawable.ic_ath_icon,
                                        0xFF4B70FF
                                )
                        )
                )
                ));


    }
}
