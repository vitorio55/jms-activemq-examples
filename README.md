# JMS example project using ActiveMQ

Project based on Alura's Java MQ course.  
A jar is required for the ActiveMQ imports to work, it is located in the lib dir.  
The ActiveMQ version used is 5.17.0  

## Usage

1 - Install ActiveMQ version 5.17.0  
2 - Start a cmd prompt (I ran it as administrator) and start ActiveMQ  

	$ cd C:\...\apache-activemq-5.17.0\bin
	$ activemq start

3 - Once running you can go to **http://localhost:8161** to access the ActiveMQ panel  
4 - Chose one of the example classes and run it as Java Application  
5 - If an authentication error happens, simple auth must be configured (see next section)  

## Queue authorization using simple auth

Add the following to ActiveMQ's file **conf/activemq.xml** (inside the **<broker>** element):

	<plugins>
	  <simpleAuthenticationPlugin anonymousAccessAllowed="false">
	    <users>
	        <authenticationUser username="admin" password="admin_pwd" groups="users,admins"/>
	        <authenticationUser username="user" password="user_pwd" groups="users"/>
	        <authenticationUser username="guest" password="guest_pwd" groups="guests"/>
	    </users>
	  </simpleAuthenticationPlugin>
	
	  <!-- authorizationPlugin follows -->
	</plugins>

For authorizations, add the following inside the **<plugins>** element:

	<authorizationPlugin>
	    <map>
	      <authorizationMap>
	        <authorizationEntries>
	          <authorizationEntry queue="fila.financeiro" read="users" write="users" admin="users,admins" />
	          <authorizationEntry topic="comercial" read="users" write="users" admin="users,admins" />
	          <authorizationEntry topic="ActiveMQ.Advisory.>" read="users,admins" write="users,admins" admin="users,admins"/>
	        </authorizationEntries>
	        <tempDestinationAuthorizationEntry>
	          <tempDestinationAuthorizationEntry read="admin" write="admin" admin="admin"/>
	        </tempDestinationAuthorizationEntry>
	      </authorizationMap>
	    </map>
	</authorizationPlugin>



## Enabling prioritized messages

In **conf/activemq.xml** file, to enable prioritizedMessages support to queues,
we must add a new entry to destionationPolicy element:

	<destinationPolicy>
		...
		<policyEntries>
			...
			<policyEntry queue=">" prioritizedMessages="true"/>
			...
		</policyEntries>
	</destinationPolicy>
