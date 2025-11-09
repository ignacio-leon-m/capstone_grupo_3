
-- Script para insertar 10 conceptos generados desde cpp.pdf

INSERT INTO conceptos (id, palabra_concepto, hint, fecha_creacion, id_tema) VALUES
(gen_random_uuid(), 'BIG_O', 'Algorithmic efficiency measure - Describes the upper bound of an algorithm''s time or space complexity', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'DATA_STRUCTURE', 'Organizing and storing data - A way of organizing and storing data in a computer efficiently', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'VECTOR', 'Dynamic array in C++ - A dynamic array that can resize itself automatically', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'ITERATOR', 'Pointer to container element - An object that enables traversal and access of elements in a container', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'QUEUE', 'FIFO data structure - First-In-First-Out principle; elements added to rear, removed from front', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'STACK', 'LIFO data structure - Last-In-First-Out principle; elements added and removed from the top', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'MAP', 'Key-value pairs - An associative container that stores unique key-value pairs', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'SET', 'Unique element collection - A container that stores unique elements in a specific order', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'BINARY_SEARCH', 'Divide and conquer search - Efficient search algorithm for sorted data that divides intervals in half', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a'),

(gen_random_uuid(), 'GREEDY', 'Optimal local choices - Makes locally optimal choices at each step to find global optimum', CURRENT_TIMESTAMP, '61cfbb4b-371a-4655-8adf-455564c3a54a');
