package com.baozistore.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baozistore.api.model.Cliente;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	// POST /clientes - cadastra um cliente novo
	@PostMapping
	public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
		// se nao mandar a data, considera que virou cliente hoje
		if (cliente.getClienteDesde() == null) {
			cliente.setClienteDesde(LocalDate.now());
		}
		Cliente salvo = clienteRepository.save(cliente);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	// GET /clientes - lista todos
	@GetMapping
	public List<Cliente> listar() {
		return clienteRepository.findAll();
	}

	// GET /clientes/{id} - busca um cliente pelo id
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);
		if (cliente.isPresent()) {
			return ResponseEntity.ok(cliente.get());
		}
		return ResponseEntity.notFound().build();
	}

	// PUT /clientes/{id} - atualiza os dados do cliente
	@PutMapping("/{id}")
	public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente dados) {
		Optional<Cliente> clienteExistente = clienteRepository.findById(id);
		if (clienteExistente.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Cliente cliente = clienteExistente.get();
		cliente.setNome(dados.getNome());
		if (dados.getClienteDesde() != null) {
			cliente.setClienteDesde(dados.getClienteDesde());
		}
		return ResponseEntity.ok(clienteRepository.save(cliente));
	}

	// DELETE /clientes/{id} - apaga o cliente
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletar(@PathVariable Long id) {
		if (!clienteRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		// nao deixa apagar se tiver pedido usando, senao da erro de chave estrangeira
		if (pedidoRepository.existsByClienteId(id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Nao e possivel apagar: existem pedidos com esse cliente");
		}
		clienteRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
