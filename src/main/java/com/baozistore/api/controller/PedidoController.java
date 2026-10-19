package com.baozistore.api.controller;

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
import com.baozistore.api.model.Pedido;
import com.baozistore.api.model.Produto;
import com.baozistore.api.repository.ClienteRepository;
import com.baozistore.api.repository.PedidoRepository;
import com.baozistore.api.repository.ProdutoRepository;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	// exemplo de json:
	// { "cliente": { "id": 1 }, "produto": { "id": 1 }, "quantidade": 10 }
	@PostMapping
	public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
		String erro = validar(pedido);
		if (erro != null) {
			return ResponseEntity.badRequest().body(erro);
		}

		// busca o cliente e o produto completos no banco
		Optional<Cliente> cliente = clienteRepository.findById(pedido.getCliente().getId());
		if (cliente.isEmpty()) {
			return ResponseEntity.badRequest().body("Cliente nao encontrado");
		}
		Optional<Produto> produto = produtoRepository.findById(pedido.getProduto().getId());
		if (produto.isEmpty()) {
			return ResponseEntity.badRequest().body("Produto nao encontrado");
		}
		if (Boolean.FALSE.equals(produto.get().getEstoque())) {
			return ResponseEntity.badRequest().body("Produto sem estoque");
		}

		pedido.setCliente(cliente.get());
		pedido.setProduto(produto.get());
		Pedido salvo = pedidoRepository.save(pedido);
		return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
	}

	@GetMapping
	public List<Pedido> listar() {
		return pedidoRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		if (pedido.isPresent()) {
			return ResponseEntity.ok(pedido.get());
		}
		return ResponseEntity.notFound().build();
	}

	// por enquanto so deixa alterar a quantidade
	@PutMapping("/{id}")
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pedido dados) {
		Optional<Pedido> pedidoExistente = pedidoRepository.findById(id);
		if (pedidoExistente.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		if (dados.getQuantidade() == null || dados.getQuantidade() <= 0) {
			return ResponseEntity.badRequest().body("Quantidade deve ser maior que zero");
		}

		Pedido pedido = pedidoExistente.get();
		pedido.setQuantidade(dados.getQuantidade());
		return ResponseEntity.ok(pedidoRepository.save(pedido));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!pedidoRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		pedidoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private String validar(Pedido pedido) {
		if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
			return "Informe o id do cliente";
		}
		if (pedido.getProduto() == null || pedido.getProduto().getId() == null) {
			return "Informe o id do produto";
		}
		if (pedido.getQuantidade() == null || pedido.getQuantidade() <= 0) {
			return "Quantidade deve ser maior que zero";
		}
		return null;
	}
}
