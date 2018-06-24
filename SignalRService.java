public class SignalRService extends Service {

    private microsoft.aspnet.signalr.client.hubs.HubConnection mHubConnection;
    private microsoft.aspnet.signalr.client.hubs.HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients

    public static final String BROADCAST_ACTION = "com.android.com.simplechatwithsignalr";

    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {
        mHubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR();
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
    public void sendMessage(String message) {
        String SERVER_METHOD_SEND = "Send";
        mHubProxy.invoke(SERVER_METHOD_SEND, message);
    }
    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        String serverUrl = getString(R.string.url_signalR); // the url to your web
        mHubConnection = new microsoft.aspnet.signalr.client.hubs.HubConnection(serverUrl);
        String SERVER_HUB_CHAT = "myHub";
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("SimpleSignalR", e.toString());
            return;
        }

        mHubProxy.on("commandText", new SubscriptionHandler1<String>() {
            @Override
            public void run(String msg) {
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra("message", msg);
                sendBroadcast(intent);
            }
        }, String.class);
    }
}