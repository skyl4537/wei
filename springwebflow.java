- 1.spring装配WebFlow{
	1.1装配流程执行器
		<!-- 流程执行器 驱动流程的执行，当用户进入一个流程时，流程执行器会为用户创建并启动一个流程执行实例，不负责加载流程定义 -->
		<flow:flow-executor id="flowExecutor" />
	1.2配置流程注册表
		<!-- 流程注册表，加载流程定义并让流程执行器能够使用，流程表会在 "/WEB-INF/flows" 目录下查找流程定义 ，
				通过base-path属性指明所有的流程都是通过id来进行引用的
				注册流程表
			-->
		<flow:flow-registry id="flowRegistry" flow-builder-services="flowBuilderServices" >
			<flow:flow-location path="/WEB-INF/flows/order/customer-flow.xml" id="customer" /> 
			<flow:flow-location path="/WEB-INF/flows/order/order-flow.xml" id="order" /> 
			<flow:flow-location path="/WEB-INF/flows/order/payment-flow.xml" id="payment" /> 
			<flow:flow-location path="/WEB-INF/flows/pizza/pizza-flow.xml" id="pizza" /> 
		</flow:flow-registry>
	1.3处理请求流程
		<!--
			DispatcherServlet一般将请求分发给控制器，对于流程而言，FlowHandlerMapper 帮助DispatcherServlet将流程请求发送给spring WebFlow
			FlowHandlerMapping是将流程请求定向到spring webflow上，响应请求的是FlowHandlerAdapter
		-->
		<bean class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping">
			<property name="flowRegistry" ref="flowRegistry" />
		</bean>
		
		<bean class="org.springframework.webflow.mvc.servlet.FlowHandlerAdapter">
			<property name="flowExecutor" ref="flowExecutor" />
		</bean>
}

- 2.springWebFlow组件{
	流程是由三个主要元素定义的:状态、转移、流程数据
	2.1状态
		springWebFlow定义了五种不同类型的状态
		状态类型				作用
		行为(Action)			行为状态是流程逻辑发生的地方
		决策(Decision)			决策状态将流程分成两个方向，会基于流程数据的评估结果确定流程的方向
		结束(End)				结束状态是流程的最后一站，一旦进入到End状态，流程就会终止
		子流程(Subflow)			子流程状态会在当前正在运行的流程上下文中启动一个新的流程
		视图(View)				视图状态会暂停流程并邀请用户参与流程(页面展示、视图中客户的操作等)
		
		视图状态<view-state>
			视图状态用于为用户展现信息并使用户在流程中发挥作用
			id属性有两个含义，1.在流程标示这个状态，2.这个状态要展现的逻辑视图名
			<view-state id="welcome"> 
			view属性 显示的指定逻辑视图名
			<view-state id="welcome" view="greeting"> 
			model属性，页面展现表单数据时可携带model对象进行视图渲染
			<view-state id="welcome" view="greeting" model="flowScope.paymentDetails">
		
		行为状态<action-state>
			行为状态一般会触发spring所管理的bean的一些方法并根据方法调用的执行结果转移到另一个状态
			<action-state id="lookupCustomer">
				<evaluate result="customer" expression="pizzaFlowActions.lookupCustomer(requestParameters.phoneNumber)"/>
				<transition to="registrationForm" on-exception="com.springinaction.pizza.service.CustomerNotFoundException"/>
				<transition to="customerReady"/>
			</action-state>
			
			<action-state>一般都会有一个<evaluate>作为子元素，<evaluate>元素给出了行为状态要做的事情即调用的方法，expression
			属性指定了进入这个状态时要评估的表达式，实例在expression使用的表达式为SpEL表达式
		
		决策状态<decision-state>
			决策状态<decision-state>将评估一个boolean类型的表达式，在两个状态转移中选择一个
			<decision-state id="checkDeilveryArea">
				<if test="pizzaFlowActions.checkDeilveryArea(customer.zipCode)"
					then="addCustomer" 
					else="deilveryWarning"
				/>
			</decision-state>
			<if>元素是决策状态的核心，是表达式进行评估的地方，true执行then,false执行else
	
		子流程状态<subflow-state>
			使用子流程将模块化的流程独立
			<!-- 调用顾客子流程 -->
			<subflow-state id="identifyCustomer" subflow="customer">
				<output name="customer" value="order.customer"/>
				<transition on="customerReady" to="buildOrder"/>
			</subflow-state>
			
		结束状态<end-state>
			当状态转移到结束状态时所有的流程多要结束
			<end-state id="customerReady" />
			
			当到达结束状态流程会结束，接下来会发生什么取决于几个元素
			1.如果结束的是子流程，那调用它的流程会从<subflow-state>继续执行，<end-state>的ID将会用作事件触发从<subflow-state>开始转移
			2.如果结束状态设置了view属性，指定的视图将会被渲染，视图可以是相对于流程路径的视图模板，如果添加"externalRrdirect:"前缀的话
				将会重定向到流程外部的页面，如果添加"flowRedirect:"将重定向到另一个流程中
			3.如果结束的不是子流程，也没有指定view属性，那么流程就结束了，浏览器最后将会加载流程的基础URL地址，当前已没有活动的流程，
				所以会开始一个新的流程实例。
	
	
	2.2转移
		转移使用<transition>元素定义,它会作为各种状态元素的子元素
		to属性用于指定流程的下一个状态
		<transition to="customerReady"/>
		on属性来指定触发转移的事件
		<transition on="phoneEntered" to="lookupCustomer"/>
		on-exception属性类似于on属性，指定了要发生转移的异常
		<transition to="registrationForm" on-exception="com.springinaction.pizza.service.CustomerNotFoundException"/>
		
		全局转移
			流程中的所有状态都会默认拥有这个状态
			<global-transitions>
				<transition on="cancel" to="cancel"/>
			</global-transitions>
		
	
	2.3流程数据
		声明变量
			流程数据保存在变量中，而变量可以在流程的各个地方进行引用。能够以多种方式创建。
			<var>元素声明变量
				customer变量可以在流程中的任意状态进行访问
				<var name="customer" class="com.springinaction.pizza.domain.Customer"/>	
			<evaluate>元素创建变量
				<evaluate>计算了一个表达式，并将结果放到了名为toppingList的变量中，这个变量是视图作用域的
				<evaluate result="viewScope.paymentTypeList" expression="T(com.springinaction.pizza.domain.PaymentType).asList()"/>
			<set>元素设置变量
				<set>元素与<evaluate>元素类似，都是将变量设置为表达式计算结果。
				<set name="flowScope.pizza" value="new com.springinaction.pizza.domain.Pizza()"/>
	
		
		定义流程数据的作用域
			流程中携带的数据会拥有不同的生命作用域和可见性，这取决于保存数据的变量本身的作用域，springWebFlow定义了五种不同的作用域
			范围					生命作用域和可见性
			conversation			最高层级的流程开始时创建，在最高层级的流程结束时销毁，被最高层级的流程和其所有的子流程所共享
			flow					当流程开始时创建，在流程结束时销毁，只有在创建它的流程中是可见的
			request 				当一个请求进入流程时创建，在流程返回时销毁
			flash					当流程开始时创建，在流程结束时销毁。在视图状态渲染后，它会被清除
			view					当进入视图状态时创建，当这个状态退出时销毁。只是视图状态内是可见的
		
		当使用<var>来声明变量时，变量始终是流程作用域的，也就是在定义变量的流程内是有效的，当使用<set><evaluate>的时候，
		作用域通过name或result属性的前缀指定
			<set name="flowScope.pizza" value="new com.springinaction.pizza.domain.Pizza()"/>
				
			
}

- 3.流程中知识点{
	流程定义文件中的第一个状态也会是流程访问中的第一个状态
	<flow>元素的start-state属性将任意状态指定为开始状态
	<flow start-state="identifyCustomer"/>
	
	jsp文件中
		<body>
			<h2>Thank you for your order</h2>
			<![CDATA[
				<a href='${flowExecutionUrl}&_eventId=finished'>Finish</a>
			]]>
		 </body>
		flowExecutionUrl变量，它包含了流程的URL地址，结束连接将一个"_eventId"参数关联到URL上，以便回到web流程中时触发finish事件。
	
	jsp文件中
	    <form:form>
			<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey} "/>
			<input type="text" name="phoneNumber"/></br>
		<!-- <input type="submit" name="_eventId_phoneEntered" value="Lookup Customer"/> -->
		<%-- <a href="${flowExecutionUrl}&_eventId=phoneEntered&phoneNumber=123456">Lookup Customer</a> --%>
			<a href="${flowExecutionUrl}&_eventId=phoneEntered">Lookup Customer</a>
		</form:form>
		
		隐藏的_flowExecutionKey 输入域，当进入视图状态时，流程暂停并等待用户操作，赋予视图流程执行key就是一种返回流程的机制，
		当用户提交表单时，流程执行key会在_flowExecutionKey输入域中返回并在流程暂停的位置进行恢复
		_eventId部分是提供给springWebFlow的一个线索，表明接下来要触发事件。
		
		<output>元素等同于java中的return语句，表明返回一个变量
		实例
			<end-state id="customerReady" >
				<output name="customer"/> <!-- 类似java return对象 -->
			</end-state>
		
	
		<on-entry>元素将构建一个支付表单并使用spel表达式在流程作用域内创建一个paymentDetails实例
		实例：
			<view-state id="takePayment" model="flowScope.paymentDetails">
				<on-entry>
					<set name="flowScope.paymentDetails" value="new com.springinaction.pizza.domain.PaymentDetails()"/>
					<evaluate result="viewScope.paymentTypeList" expression="T(com.springinaction.pizza.domain.PaymentType).asList()"/>
				</on-entry>
				
				<transition on="paymentSubmitted" to="verifyPayment"/>
				<transition on="cancel" to="cancel"/>
				
			</view-state>
		
	
}




























