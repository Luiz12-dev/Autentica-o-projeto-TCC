-- V2: telefone do usuario.
--
-- O Core nao tem cadastro de clientes: ele provisiona o Client a partir dos
-- claims do JWT. Sem telefone aqui, o painel do dono mostrava "00000000000"
-- fixo para todo mundo, e o barbeiro nao tinha como ligar para o cliente.
--
-- Nulo e permitido porque os usuarios ja cadastrados nao tem esse dado.
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
