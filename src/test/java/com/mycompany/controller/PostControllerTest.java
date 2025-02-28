package com.mycompany.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

import com.mycompany.entity.Post;
import com.mycompany.entity.Tag;
import com.mycompany.entity.User;
import com.mycompany.service.PostService;
import com.mycompany.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class PostControllerTest {
	
	@Autowired
	private PostController postController ; 
	@Autowired
	private UserService userService;
	@Autowired
	private PostService postService; 
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void contextLoads() throws Exception{
		assertThat(postController).isNotNull();
	}
	
	@Test
	@Transactional
	@Order(1)
	public void validHTTPResponse() throws Exception{
		this.mockMvc.perform(get("/post/create")).andExpect(status().is3xxRedirection());
		this.mockMvc.perform(get("/post/update")).andExpect(status().is3xxRedirection());
		this.mockMvc.perform(get("/post/report")).andExpect(status().is3xxRedirection());

	}
	
	
	@Test
	@Transactional
	@Order(2)
	public void savePostTest() {
		// Creating Post
				Post post = new Post() ; 
				post.setType("Required") ; 

				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				    Date parsedDate = dateFormat.parse("2000-01-01 00:00:01");
				    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
					post.setDateTime(timestamp);
				}catch(Exception e) {
					System.out.println("Error : In Allocation the Time Stamp for Post");
					e.printStackTrace();
				}
				post.setMessage("medicine required");
				
				// Creating User for Post
				User user = new User() ; 
				user.setId(1);
				user.setUsername("Champ");
				user.setEmail("Champ@gmail.com");
				user.setFirstname("Champ");
				user.setLastname("OK");
				user.setPassword("Thor");
				user.setMobile("1123456789") ; 
				try {
				    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				    Date parsedDate = dateFormat.parse(String.valueOf("2000-01-01"));
				    user.setDateOfBirth(parsedDate);
				} catch(Exception e) { 
					System.out.println("Error : In Allocation of DOB to user");
					e.printStackTrace();
				}
				user.setGender("male");
				user.setEnabled(1);
				user.setWarnings(0);
				// Saving User in Database 
				userService.addUser(user);
				
				// User Authentication
				UsernamePasswordAuthenticationToken authReq
					    = new UsernamePasswordAuthenticationToken("Champ", "Thor");
				AuthenticationManager auth = new AuthenticationManager() {
							
					@Override
					public Authentication authenticate(Authentication authentication) throws AuthenticationException {
						return authentication;
					}
				};
				SecurityContext sc = SecurityContextHolder.getContext();
				sc.setAuthentication(auth.authenticate(authReq));
				
				// Generating Tags
				Set<Tag> tagsObj = new HashSet<Tag>() ; 
				tagsObj.add(new Tag("#Available" , null )) ; 
				tagsObj.add(new Tag("#Urgent" , null )) ; 
				
				// Adding Tags --> Post
				post.setTags(tagsObj);
				post.setTagStr(post.getDateTime().toString()+", Urgent, ");
				// Saving Post to Database 
				assertDoesNotThrow(() -> postController.savePost(post) );   
	}
	
	@Test
	@Transactional
	@Order(3)
	public void updatePostTest() throws Exception {
		// User Authentication
				UsernamePasswordAuthenticationToken authReq
					      = new UsernamePasswordAuthenticationToken("Champ", "Thor");
				AuthenticationManager auth = new AuthenticationManager() {
							
				@Override
				public Authentication authenticate(Authentication authentication) throws AuthenticationException {
						return authentication;
					}
				};    
				SecurityContext sc = SecurityContextHolder.getContext();
				sc.setAuthentication(auth.authenticate(authReq));
				List<Post> posts = postService.findPostByUsername("Champ") ; 
				Post post = posts.get(0) ; 
				
				
						
				String id = "" + post.getId() + "" ; 
				RequestBuilder request = get("/post/update").param("id" , id ) ;
				this.mockMvc.perform(request).andDo(print()).andExpect(status().isOk()) ; 
				
				System.out.println(post);
				String message = "All Fine" ; 
				post.setMessage(message);
				post.setTagStr("Urgent, ");
				request = post("/post/update").flashAttr("post", post) ;
				this.mockMvc.perform(request).andDo(print()).andExpect(status().is3xxRedirection()) ; 
				
				// Report Post 
				request = post("/post/report").param("id" , "" + post.getId() ) ;
				this.mockMvc.perform(request).andDo(print()).andExpect(status().is3xxRedirection()) ; 
				
				
				
				
				// Show Post
//				authReq
//			      = new UsernamePasswordAuthenticationToken("Champ", "Thor");
//				auth = new AuthenticationManager() {
//							
//				@Override
//				public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//						return authentication;
//					}
//				};    
//				sc = SecurityContextHolder.getContext();
//				sc.setAuthentication(auth.authenticate(authReq));
//				System.out.println(post);
//				request = get("/post/{id}" , "" + post.getId() ) ;
//				this.mockMvc.perform(request).andDo(print()).andExpect(status().is2xxSuccessful()) ; 
				
				
				// Delete Post
				// User Authentication
				authReq
					      = new UsernamePasswordAuthenticationToken("Champ", "Thor");
				auth = new AuthenticationManager() {
							
				@Override
				public Authentication authenticate(Authentication authentication) throws AuthenticationException {
						return authentication;
					}
				};    
				sc = SecurityContextHolder.getContext();
				sc.setAuthentication(auth.authenticate(authReq));
				System.out.println(post);
				postController.deletePosts(post.getId()) ; 
				
				// Delete User
				userService.deleteUserAccount("Champ") ; 
	}
	
	

}