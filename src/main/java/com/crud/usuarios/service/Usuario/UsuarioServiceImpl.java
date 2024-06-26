package com.crud.usuarios.service.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crud.usuarios.model.dto.ResponseModel;
import com.crud.usuarios.model.dto.UsuarioDto;
import com.crud.usuarios.model.entities.Usuario;
import com.crud.usuarios.repository.Usuario.UsuarioRepository;
import com.crud.usuarios.utilities.UsuarioMapper;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;

    //---------GET---------//
    @Override
    public List<UsuarioDto> getAllUsuarios(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(usuarioMapper::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> getUsuarioById(Integer id){
        return usuarioRepository.findById(id);
    }

    //---------POST---------//
    @Override
    public ResponseModel createUsuario(UsuarioDto usuarioDto){
        var existeEmail = usuarioRepository.findByemail(usuarioDto.getEmail());
        if (!existeEmail.isEmpty()) {
            return new ResponseModel(false, "Ya existe un usuario con el email '" + usuarioDto.getEmail()+ "'");
        }
        Usuario usuario = usuarioMapper.convertirAEntity(usuarioDto);//Mapeo
        var resultado = usuarioRepository.save(usuario);
        return new ResponseModel(true, "Usuario creado con éxito. Id: " + resultado.getIdUsuario());
    }

    @Override
    public ResponseModel validarLogin(String email, String contrasena){
        boolean status = false;
        String message = "";

        var usuario = usuarioRepository.findByemail(email);
        if (!usuario.isEmpty()) {
            if (usuario.get().getcontrasena().equals(contrasena)) {
                status = true;
                message = "Login realizado con éxito.";
            }else{
                message = "Usuario y/o contraseña no válidos.";
            }
        }else{
            message = "No existe un usuario asociado al email " + email;
        }
       

        return new ResponseModel(status, message);
    }

    //---------PUT---------//
    @Override
    public ResponseModel updateUsuario(Integer id, UsuarioDto usuarioDto){
        var usuarioExiste = usuarioRepository.findById(id);
        if (!usuarioExiste.isEmpty()) {
            Usuario usuario = usuarioExiste.get();
            usuario.setApellidoMaterno(usuarioDto.getApellidoMaterno());
            usuario.setApellidoPaterno(usuarioDto.getApellidoPaterno());
            usuario.setDireccion(usuarioDto.getDireccion());
            usuario.setEmail(usuarioDto.getEmail());
            usuario.setNombre(usuarioDto.getNombre());
            usuario.setPerfil(usuarioDto.getPerfil());
            usuario.setTelefono(usuarioDto.getTelefono());
            usuario.setcontrasena(usuarioDto.getContrasena());
            usuario.setIdUsuario(id);
            //Actualizar usuario
            var resultado = usuarioRepository.save(usuario);
            return new ResponseModel(true, "Usuario actualizado con éxito. Id: " + resultado.getIdUsuario());
        }else{
            return new ResponseModel(false, "El usuario ingresado no existe. Id: " + id);
        }
    }

    //---------DELETE---------//
    @Override
    public ResponseModel deleteUsuario(Integer id){
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return new ResponseModel(true, "Usuario eliminado con éxito");
        }else{
            return new ResponseModel(false, "El usuario ingresado no existe");
        }
    }
}
