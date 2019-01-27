 [HubName("myHub")]
    public class MySignalHub : Microsoft.AspNet.SignalR.Hub
    {
        private static ILog log = LogManager.GetLogger(typeof(MySignalHub));

        public static void SendToAll(string command)
        {
            IHubContext hubContext = GlobalHost.ConnectionManager.GetHubContext<MySignalHub>();
            try
            {
                hubContext.Clients.All.commandText(command);
            }
            catch (Exception ex)
            {
                log.Error(ex);
            }
        }
    }