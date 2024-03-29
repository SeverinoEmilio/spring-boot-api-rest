package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {
	
	private TokenService tokenService;
	private UsuarioRepository usuarioRepository;
	
	

	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		String token = recuperarToken(request);
		
		boolean valido = tokenService.isTokenValido(token);
		
		System.out.println(valido);
		
		if(valido) {
			autenticarCliente(token);
		}
		
		
		filterChain.doFilter(request, response);
		
	}

	private void autenticarCliente(String token) {
		// TODO Auto-generated method stub
		
		Long idUsuario = tokenService.getIdUsuario(token);
		
		Usuario usuario = this.usuarioRepository.findById(idUsuario).get();
		
		UsernamePasswordAuthenticationToken  authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
	}

	private String recuperarToken(HttpServletRequest request) {
		// TODO Auto-generated method stub
		System.out.println("Header:"+ request.getHeader("Authorization"));
		String token = request.getHeader("Authorization");
		
		if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}

}
