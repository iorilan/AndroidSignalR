protected void onCreate(Bundle savedInstanceState){
	
	...
	 /*
        * Web socket
        * */
        Intent intent = new Intent();
        intent.setClass(mContext, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(broadcastReceiver, new IntentFilter(SignalRService.BROADCAST_ACTION));
...
}


private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SenseCallbackModel ret = null;
            try{
                String message = intent.getStringExtra("message").toString();
                Gson gson = new GsonBuilder().create();
                //// fire and forget
                ret = gson.fromJson(message, SenseCallbackModel.class);
            }catch (Exception ex){
                Log.e("ERROR",ex.getMessage());
                UtilApp.showPopupMsg(mContext, "error occured when deserialize");
            }

            if(ret != null){
					....
                // do something with result
            }
            //UtilApp.showPopupMsg(mContext,message);
        }
    };

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };