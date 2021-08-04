<%@ include file="parts/meta.jsp"%>

<title>Home Page</title>

<%@ include file="parts/header.jsp"%>
<h1>Welcome to Covid Resource Manager</h1>

<c:forEach items="${posts }" var="post" varStatus="tagStatus">

	<c:if test="${username != post.user.username }">
		<a href="/user/profile?username=${post.user.username }"><b>@${post.user.username }</b>
		</a>
	</c:if>
	<c:if test="${username == post.user.username }">
		<a href="/user/profile"><b>@${post.user.username }</b> </a>
	</c:if>

	<br>                               
Post Type: ${post.type }
<c:if test="${username == post.user.username }">
		<a href="/post/update/${post.id }">
			<button>Update Post</button>
		</a>
		<a href="/post/delete/${post.id }">
			<button>Delete Post</button>
		</a>
	</c:if>
	<br>
Post Message: ${post.message }<br>
Post Tags: 
<c:forEach items="${post.tags }" var="tag" varStatus="tagStatus">
${tag.name }
</c:forEach>
	<br>
Post Comments:<br>
	<c:forEach items="${post.comments }" var="comment"
		varStatus="tagStatus">
Comment By: <c:if test="${username != comment.user.username }">
			<a href="/user/profile?username=${comment.user.username }"><b>@${comment.user.username }</b>
			</a>
		</c:if>
		<c:if test="${username == comment.user.username }">
			<a href="/user/profile"><b>@${comment.user.username }</b> </a>
		</c:if>
		<br>
Comment Comment: ${comment.content }<br>
Comment At: ${comment.dateTime }<br>
		<br>
	</c:forEach>
	<c:if test="${username != null }">
		<sf:form modelAttribute="comment">
			<sf:input type="hidden" path="post" value="${ post.id}" />
			<sf:input path="content" />
			<sf:button name="Submit" value="Add Comment">Add Comment</sf:button>
		</sf:form>
	</c:if>
	<br>
	<br>
	<br>
</c:forEach>

<%@ include file="parts/footer.jsp"%>

<script>
	function displayForm(postId) {
		var comment_form = document.getElementById("COMMENT_FORM");
		var br = document.createElement("br");
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", "/comment/create");
		var a = document.createElement("input");
		a.setAttribute("type", "hidden");
		a.setAttribute("name", "${_csrf.parameterName}");
		a.setAttribute("value", "${_csrf.token}");
		var commentContent = document.createElement("input");
		commentContent.setAttribute("type", "text");
		commentContent.setAttribute("name", "content");
		var s = document.createElement("input");
		s.setAttribute("type", "submit");
		s.setAttribute("value", "Post Comment");
		form.appendChild(a);
		form.appendChild(commentContent);
		form.appendChild(br);
		form.appendChild(s);
		comment_form.appendChild(form);
	}
</script>