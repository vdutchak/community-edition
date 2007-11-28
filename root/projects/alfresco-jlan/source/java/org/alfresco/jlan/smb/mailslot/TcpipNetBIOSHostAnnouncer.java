package org.alfresco.jlan.smb.mailslot;

/*
 * HostAnnouncer.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.alfresco.jlan.netbios.NetBIOSDatagram;
import org.alfresco.jlan.netbios.NetBIOSDatagramSocket;
import org.alfresco.jlan.netbios.NetBIOSName;
import org.alfresco.jlan.netbios.NetworkSettings;
import org.alfresco.jlan.netbios.RFCNetBIOSProtocol;

/**
 * <p>TCP/IP NetBIOS host announcer implementation. Periodically broadcasts a host announcement datagram to inform other
 * Windows networking hosts of the local hosts existence and capabilities.
 */
public class TcpipNetBIOSHostAnnouncer extends HostAnnouncer {

  //	Default port and announcement interval

  public static final int PORT 			= RFCNetBIOSProtocol.DATAGRAM;
  public static final int INTERVAL 	= 1; // minutes

  //	Local address to bind to, port to use

  private InetAddress m_bindAddress;
  private int m_port;

	//	Broadcast address and port
	
	private InetAddress m_bcastAddr;
	private int m_bcastPort = RFCNetBIOSProtocol.DATAGRAM;
	
  //	NetBIOS datagram

  private NetBIOSDatagram m_nbdgram;

  /**
   * Default constructor.
   */
  public TcpipNetBIOSHostAnnouncer() {

    //  Set the default port and interval

    setPort(PORT);
    setInterval(INTERVAL);
  }

  /**
   * Create a host announcer.
   *
   * @param name    Host name to announce
   * @param domain  Domain name to announce to
   * @param intval  Announcement interval, in minutes
   * @param port		Port to use
   */
  public TcpipNetBIOSHostAnnouncer(String name, String domain, int intval, int port) {
    
		//	Add the host to the list of names to announce
		
    addHostName(name);
    setDomain(domain);
    setInterval(intval);
    
    //	If port is zero then use the default port
    
    if ( port == 0)
      setPort(PORT);
    else
      setPort(port);
  }

  /**
   * Get the local address that the announcer should bind to.
   *
   * @return java.net.InetAddress
   */
  public final InetAddress getBindAddress() {
    return m_bindAddress;
  }

  /**
   * Return the socket/port number that the announcer is using.
   *
   * @return int
   */
  public final int getPort() {
    return m_port;
  }

  /**
   * Check if the announcer should bind to a particular local address, or all
   * local addresses.
   *
   * @return boolean
   */
  public final boolean hasBindAddress() {
    return m_bindAddress != null ? true : false;
  }

	/**
	 * Set the broadcast address
	 * 
	 * @param addr String
	 * @exception UnknownHostException
	 */
	public final void setBroadcastAddress(String addr)
		throws UnknownHostException {
	  m_bcastAddr = InetAddress.getByName ( addr);
	}
	
	/**
	 * Set the broadcast address and port
	 * 
	 * @param addr String
	 * @param port int
	 * @exception UnknownHostException
	 */
	public final void setBroadcastAddress(String addr, int port)
		throws UnknownHostException {
	  m_bcastAddr = InetAddress.getByName ( addr);
	  m_bcastPort = port;
	}
	
  /**
   * Initialize the host announcer.
   * 
   * @exception Exception
   */
  protected void initialize()
  	throws Exception {

    //  Set this thread to be a daemon, set the thread name

    if ( hasBindAddress() == false)
      setName("TCPHostAnnouncer");
    else
      setName("TCPHostAnnouncer_" + getBindAddress().getHostAddress());
    
    //  Check if at least one host name has been set, if not then use the local host name

    if (numberOfNames() == 0) {

      //  Get the local host name

      addHostName(InetAddress.getLocalHost().getHostName());
    }

    //  Allocate the NetBIOS datagram

    m_nbdgram = new NetBIOSDatagram(512);
    
    //	If the broadcast address has not been set, generate a broadcast address

		if ( m_bcastAddr == null)
    	m_bcastAddr = InetAddress.getByName(NetworkSettings.GenerateBroadcastMask(null));
  }

  /**
   * Determine if the network connection used for the host announcement is valid
   * 
   * @return boolean
   */
  public boolean isNetworkEnabled() {
    return true;
  }
  
  /**
   * Send an announcement broadcast.
   *
   * @param hostName 	Host name being announced
   * @param buf				Buffer containing the host announcement mailslot message.
   * @param offset		Offset to the start of the host announcement message.
   * @param len				Host announcement message length.
   */
  protected void sendAnnouncement( String hostName, byte[] buf, int offset, int len)
  	throws Exception {
    
    //  Send the host announce datagram

		m_nbdgram.SendDatagram(NetBIOSDatagram.DIRECT_GROUP, hostName, NetBIOSName.FileServer, getDomain(),
													 NetBIOSName.MasterBrowser, buf, len, offset);
  }

  /**
   * Set the local address to bind to.
   *
   * @param addr java.net.InetAddress
   */
  public final void setBindAddress(InetAddress addr) {
    m_bindAddress = addr;
    NetBIOSDatagramSocket.setBindAddress(addr);
  }

  /**
   * Set the socket/port number to use.
   *
   * @param port int
   */
  public final void setPort(int port) {
    m_port = port;
    NetBIOSDatagramSocket.setDefaultPort(port);
  }
}