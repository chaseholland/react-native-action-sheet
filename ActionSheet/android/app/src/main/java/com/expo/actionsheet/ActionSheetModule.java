package com.expo.actionsheet;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;

public class ActionSheetModule extends ReactContextBaseJavaModule {

    private final static String TAG = ActionSheetModule.class.getSimpleName();

    BottomSheetDialog actionSheetDialog;

    public ActionSheetModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "ActionSheet";
    }

    @ReactMethod
    public void showActionSheetWithOptions(ReadableMap options, final Callback onSelect) {
        ReadableArray optionArray = options.getArray("options");
        String title = "";
        String tintColor = "";
        
        if(options.hasKey("title")) {
            title = options.getString("title");
        }
        if(options.hasKey("tintColor")) {
            tintColor = options.getString("tintColor");
        }

        ArrayList<String> optionsTitles = new ArrayList<>();
        for (int i = 0; i < optionArray.size(); i++) {
            optionsTitles.add(optionArray.getString(i));
        }
        actionSheetDialog = new BottomSheetDialog(getCurrentActivity());
        View sheetView = getCurrentActivity().getLayoutInflater().inflate(R.layout.action_sheet_view, null);
        final ListView listView = (ListView) sheetView.findViewById(R.id.action_sheet_list_view);
        final TextView titleView = (TextView) sheetView.findViewById(R.id.action_sheet_title);

        if(!title.isEmpty()){
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        }

        SheetItemAdapter adapter = new SheetItemAdapter(getReactApplicationContext(), optionsTitles, tintColor, onSelect);
        listView.setAdapter(adapter);

        actionSheetDialog.setContentView(sheetView);
        actionSheetDialog.show();
    }


    public class SheetItemAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<String> options;
        private final Callback onSelect;
        private final String tintColor;

        SheetItemAdapter(Context context, ArrayList<String> options, String tintColor, final Callback onSelect) {
            this.context = context;
            this.options = options;
            this.onSelect = onSelect;
            this.tintColor = tintColor;
        }

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.action_sheet_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.action_sheet_item_text);
            textView.setText(options.get(position));

            if(!tintColor.isEmpty()) {
                textView.setTextColor(Color.parseColor(tintColor));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Item Clicked " + position);
                    //Toast.makeText(getReactApplicationContext(), options.get(position), Toast.LENGTH_SHORT).show();
                    onSelect.invoke(position);
                    actionSheetDialog.dismiss();
                }
            });
            return convertView;
        }
    }
}