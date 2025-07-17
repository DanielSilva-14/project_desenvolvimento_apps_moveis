-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 20-Jun-2024 às 02:38
-- Versão do servidor: 10.4.28-MariaDB
-- versão do PHP: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `summithub`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `articles`
--

CREATE TABLE `articles` (
  `id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `date_published` datetime NOT NULL,
  `session_id` int(11) NOT NULL,
  `abstract` text NOT NULL,
  `pdf` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `articles`
--

INSERT INTO `articles` (`id`, `title`, `date_published`, `session_id`, `abstract`, `pdf`) VALUES
(1, 'Threat Detection and Response', '2024-06-10 00:00:00', 1, 'This research examines the application of artificial intelligence (AI) in threat detection and response within cybersecurity. The study aims to develop AI-driven solutions that enhance the ability to identify, analyze, and mitigate cyber threats in real-time. The primary results demonstrate significant improvements in threat detection accuracy, speed of response, and the ability to handle complex and evolving cyber threats. The main conclusions suggest that AI has the potential to revolutionize cybersecurity by providing more proactive and adaptive defense mechanisms.', 'Threat%20Detection%20and%20Response.pdf'),
(2, 'Techniques for Intrusion Detection', '2024-06-04 00:00:00', 1, 'This research investigates various artificial intelligence (AI) techniques applied to intrusion detection systems (IDS) in cybersecurity. The study aims to evaluate the effectiveness of AI-driven approaches in identifying and mitigating unauthorized access and malicious activities within networks. The primary results highlight significant improvements in detection accuracy, response times, and adaptability to new threats when AI techniques are employed. The main conclusions suggest that AI-based intrusion detection can substantially enhance network security, providing more sophisticated and proactive defense mechanisms compared to traditional methods.', 'Techniques%20for%20Intrusion%20Detection.pdf'),
(3, 'Service Robots: Challenges', '2024-06-14 00:00:00', 2, 'This research investigates the challenges associated with the development and deployment of service robots utilizing artificial intelligence (AI). The study aims to identify the key technical, ethical, and societal obstacles that hinder the effective implementation of AI-driven service robots. The primary results highlight significant challenges in areas such as perception, navigation, human-robot interaction, and ethical considerations. The main conclusions suggest that while AI offers substantial potential for advancing service robotics, addressing these challenges is crucial for realizing their full benefits in various service sectors.', 'Service%20Robots%20Challenges.pdf'),
(4, 'Robots in Dynamic Environments', '2024-06-11 00:00:00', 2, 'This research explores the application of artificial intelligence (AI) in enabling robots to operate effectively in dynamic environments. The study aims to identify and address the challenges faced by robots when interacting with unpredictable and changing surroundings. The primary results demonstrate advancements in AI techniques that enhance robotic perception, decision-making, and adaptability. The main conclusions suggest that AI-driven robots have significant potential to function autonomously in dynamic environments, but overcoming technical obstacles is essential for achieving reliable and robust performance.\r\n', 'Robots%20in%20Dynamic%20Environments.pdf'),
(5, 'Game Design: Content Generation', '2024-06-15 00:00:00', 3, 'This research explores the role of artificial intelligence (AI) in content generation for video games. The study investigates how AI techniques are employed to create dynamic and engaging game content, including levels, quests, characters, and narratives. The primary results highlight advancements in procedural generation, machine learning, and natural language processing that enable AI-driven content creation. The main conclusions suggest that AI has the potential to revolutionize game design by providing developers with tools to generate vast amounts of diverse and personalized content, enhancing player experience and replayability.', 'Game%20Design%20Content%20Generation.pdf'),
(6, 'AI-driven Player Behavior', '2024-06-07 00:00:00', 3, 'This research explores the application of artificial intelligence (AI) in modeling and predicting player behavior in video games. The study investigates how AI techniques, such as machine learning and reinforcement learning, are utilized to analyze player actions, preferences, and interactions within game environments. The primary results highlight advancements in AI-driven player behavior modeling, which enable personalized gameplay experiences, dynamic difficulty adjustment, and enhanced player engagement. The main conclusions suggest that AI has the potential to transform game design by tailoring experiences to individual players, improving game balance, and fostering long-term player satisfaction.', 'AI-driven%20Player%20Behavior.pdf'),
(7, 'AI-Assisted Medical Diagnosis', '2024-06-01 00:00:00', 4, 'This research investigates the application of artificial intelligence (AI) in medical diagnosis, aiming to enhance diagnostic accuracy and efficiency. The study develops AI models trained on large datasets of medical records, imaging data, and patient history to assist in diagnosing various diseases. The primary results show that AI-assisted diagnosis significantly improves the accuracy of detecting diseases such as cancer, cardiovascular disorders, and neurological conditions. The main conclusions indicate that AI can serve as a valuable tool in clinical settings, augmenting the capabilities of healthcare professionals and leading to better patient outcomes.\r\n', 'AI-Assisted%20Medical%20Diagnosis.pdf'),
(8, 'Predict diseases based on medical data', '2024-05-26 00:00:00', 4, 'This research explores the application of artificial intelligence (AI) to predict diseases based on medical data. The study aims to develop predictive models that leverage electronic health records (EHRs), genetic information, and lifestyle data to forecast the onset of various diseases. The primary results indicate significant improvements in early disease prediction accuracy, particularly for chronic conditions such as diabetes, cardiovascular diseases, and certain cancers. The main conclusions highlight the potential of AI-driven predictive analytics to enhance preventative healthcare, allowing for earlier interventions and better patient outcomes.', 'Predict%20diseases%20based%20on%20medical%20data.pdf'),
(9, 'Optimizing Traffic Flow with AI', '2024-06-02 00:00:00', 5, 'This research focuses on optimizing traffic flow using artificial intelligence (AI) techniques. The study aims to develop AI models capable of dynamically managing and improving traffic conditions in urban environments. Through simulations and realworld testing, the research demonstrates significant improvements in traffic efficiency and reduction in congestion. Key findings include a 25% increase in average vehicle speed and a 20% decrease in travel time across congested areas. These results underscore the potential of AI-driven solutions to mitigate urban traffic challenges and enhance overall transportation efficiency.', 'Optimizing%20Traffic%20Flow%20with%20AI.pdf'),
(10, 'Deep Learning for Autonomous Vehicles', '2024-02-10 00:00:00', 5, 'This research develops a novel deep learning model specifically designed for \r\nautonomous vehicles, aiming to enhance road safety and fuel efficiency. The proposed model employs an advanced convolutional neural network (CNN) augmented with sensor fusion techniques to integrate data from cameras, LIDAR, and radar. Following a rigorous training process using an extensive dataset comprising millions of miles of driving data across diverse scenarios, the model was tested in real-worldconditions. Results demonstrated a 30% reduction in accidents and a 15% improvement in fuel efficiency. These findings suggest that the deep learning model can significantly enhance the operation of autonomous vehicles, indicating substantial potential for broader adoption within the automotive industry.', 'Deep%20Learning%20for%20Autonomous%20Vehicles.pdf'),
(11, 'Intelligent Systems: Enhancing Learning', '2024-05-21 00:00:00', 6, 'This research examines the application of intelligent systems to enhance learning processes and outcomes. By integrating artificial intelligence (AI) and machine learning (ML) technologies into educational environments, the study aims to create adaptive learning systems that personalize education for individual students. The primary results indicate that these systems can significantly improve student engagement, comprehension, and retention of material. The main conclusions suggest that intelligent systems hold substantial potential to transform traditional educational methods, fostering more effective and inclusive learning experiences.\r\n', 'intelligentSystemsEnhancingLearning.pdf'),
(12, 'Identifying At-Risk Students', '2024-05-19 00:00:00', 6, 'This research investigates the application of artificial intelligence (AI) to identify at-risk students in educational settings. The study aims to develop predictive models that analyze academic performance, attendance records, socio-economic factors, and behavioral data to identify students who are at risk of academic failure or dropping out. The primary results indicate significant improvements in early detection accuracy, enabling timely interventions. The main conclusions suggest that AI-driven approaches can significantly enhance student support systems, improving retention rates and overall academic success.\r\n', 'IdentifyingAt-RiskStudents.pdf'),
(13, 'Business Process Automation', '2024-05-23 00:00:00', 7, 'This research examines the application of artificial intelligence (AI) in business process automation (BPA). The study aims to develop AI-driven solutions to automate repetitive and complex business processes, thereby improving efficiency, reducing errors, and lowering operational costs. The primary results indicate significant enhancements in process speed, accuracy, and overall productivity. The main conclusions suggest that AI-driven BPA can transform organizational workflows, leading to substantial gains in efficiency and competitive advantage.', 'Business%20Process%20Automation.pdf'),
(14, 'Economic Impact of AI', '2024-05-29 00:00:00', 7, 'This research explores the economic impact of artificial intelligence (AI) across various sectors. The study aims to quantify the contributions of AI to economic growth, productivity, and job creation, while also addressing the challenges and risks associated with AI adoption. The primary results indicate that AI can significantly boost economic output and efficiency, particularly in industries such as manufacturing, healthcare, and finance. The main conclusions suggest that while AI presents substantial economic opportunities, it also necessitates proactive policy measures to mitigate potential negative effects on employment and inequality.\r\n', 'Economic%20Impact%20of%20AI.pdf'),
(15, 'Current and Predictions in AI', '2024-05-26 00:00:00', 8, 'This research examines the current state of artificial intelligence (AI) and makes predictions about its future developments and impacts. The study aims to provide a comprehensive overview of the latest advancements in AI technologies, their applications, and the emerging trends that are likely to shape the future of AI. The primary results highlight significant progress in areas such as natural language processing (NLP), computer vision, and reinforcement learning. The main conclusions suggest that AI will continue to evolve rapidly, with profound implications for various industries, society, and ethical considerations.', 'Current%20and%20Predictions%20in%20AI.pdf'),
(16, 'Neurosymbolic AI with Deep Learning', '2024-05-10 00:00:00', 8, 'This research investigates the integration of neurosymbolic AI with deep learning, aiming to combine the strengths of symbolic reasoning and neural networks. The study develops hybrid models that leverage symbolic logic for structured, interpretable reasoning, while employing deep learning for robust pattern recognition and data-driven learning. The primary results demonstrate that neurosymbolic AI can achieve higher accuracy and interpretability in complex tasks compared to purely symbolic or neural approaches. The main conclusions suggest that neurosymbolic AI has the potential to significantly advance the field of artificial intelligence, providing more comprehensive and interpretable solutions to a range of problems.', 'Neurosymbolic%20AI%20with%20Deep%20Learning.pdf');

-- --------------------------------------------------------

--
-- Estrutura da tabela `article_authors`
--

CREATE TABLE `article_authors` (
  `article_id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `article_authors`
--

INSERT INTO `article_authors` (`article_id`, `author_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10),
(11, 12),
(12, 13),
(13, 14),
(14, 15),
(15, 16),
(16, 17);

-- --------------------------------------------------------

--
-- Estrutura da tabela `article_questions`
--

CREATE TABLE `article_questions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `article_id` int(11) NOT NULL,
  `content` text NOT NULL,
  `approved` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `article_questions`
--

INSERT INTO `article_questions` (`id`, `user_id`, `article_id`, `content`, `approved`) VALUES
(7, 6, 1, 'ddddd', 1);

-- --------------------------------------------------------

--
-- Estrutura da tabela `authors`
--

CREATE TABLE `authors` (
  `id` int(11) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `birthdate` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `authors`
--

INSERT INTO `authors` (`id`, `fullname`, `birthdate`) VALUES
(1, 'Jonathan Lee', '1975-09-15 00:00:00'),
(2, 'Jessica Wang', '1983-03-22 00:00:00'),
(3, 'Olivia Roberts', '1988-07-08 00:00:00'),
(4, 'Matthew Green', '1979-11-05 00:00:00'),
(5, 'Robert Davis', '1972-02-17 00:00:00'),
(6, 'Mark Riedl', '1981-04-03 00:00:00'),
(7, 'Adam Wilson', '1974-06-14 00:00:00'),
(8, 'Sarah Thompson', '1986-10-29 00:00:00'),
(9, 'Daniel Evans', '1980-01-12 00:00:00'),
(10, 'Ana Silva', '1982-05-07 00:00:00'),
(12, 'Jennifer Brown', '1984-12-16 00:00:00'),
(13, 'Andrew Miller', '1989-04-09 00:00:00'),
(14, 'Samantha Clarke', '1976-07-01 00:00:00'),
(15, 'Michael Harris', '1978-03-21 00:00:00'),
(16, 'Jürgen Gall', '1970-02-05 00:00:00'),
(17, 'Sebastian Ruder', '1983-11-19 00:00:00');

-- --------------------------------------------------------

--
-- Estrutura da tabela `conferences`
--

CREATE TABLE `conferences` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `information` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `conferences`
--

INSERT INTO `conferences` (`id`, `name`, `information`) VALUES
(1, 'Inovação e Impacto: Conferência de Tecnologia AI 2024', 'Explore o futuro da tecnologia e inteligência artificial na \"Inovação e Impacto: Conferência de Tecnologia AI 2024\". Esta conferência multidisciplinar reunirá líderes industriais e académicos para debater as mais recentes pesquisas e inovações em áreas críticas como Cibersegurança, Robótica, Videojogos, Saúde, Transportes, Educação, Negócios e Economia. Com sessões que abrangem desde a detecção de ameaças cibernéticas até ao aprendizado profundo para veículos autónomos, a conferência oferecerá visões sobre como a AI está a moldar o futuro em diversos campos. Junte-se a nós para sessões informativas, debates estimulantes e a oportunidade de conectar-se com especialistas e profissionais de todo o mundo.');

-- --------------------------------------------------------

--
-- Estrutura da tabela `rooms`
--

CREATE TABLE `rooms` (
  `id` int(11) NOT NULL,
  `name` varchar(4) NOT NULL,
  `address` varchar(300) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `rooms`
--

INSERT INTO `rooms` (`id`, `name`, `address`, `latitude`, `longitude`) VALUES
(1, 'INE', 'Campus de Campolide, 1070-312 Lisboa', 38.73228777223875, -9.160242771589989),
(2, 'A14', 'Campus de Campolide, 1070-312 Lisboa', 38.73231448117042, -9.160662456907865),
(3, 'A120', 'Campus de Campolide, 1070-312 Lisboa', 38.73301117219658, -9.159733830787278),
(4, 'AA1', 'Campus de Campolide, 1070-312 Lisboa', 38.732779652232814, -9.159309583440347),
(5, 'AA2', 'Campus de Campolide, 1070-312 Lisboa', 38.73267133443007, -9.159248420697251);

-- --------------------------------------------------------

--
-- Estrutura da tabela `sessions`
--

CREATE TABLE `sessions` (
  `id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `datetime_start` datetime NOT NULL,
  `datetime_end` datetime NOT NULL,
  `room_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `sessions`
--

INSERT INTO `sessions` (`id`, `title`, `datetime_start`, `datetime_end`, `room_id`) VALUES
(1, 'Cybersecurity', '2024-07-01 10:00:00', '2024-07-01 11:00:00', 1),
(2, 'Robotics', '2024-07-01 10:00:00', '2024-07-01 11:00:00', 2),
(3, 'Videogames', '2024-07-01 14:00:00', '2024-07-01 15:00:00', 3),
(4, 'Healthcare', '2024-07-02 10:00:00', '2024-07-02 11:00:00', 3),
(5, 'Transportation', '2024-07-02 10:00:00', '2024-07-02 11:00:00', 1),
(6, 'Education', '2024-07-02 14:00:00', '2024-07-02 15:00:00', 2),
(7, 'Business and Economy', '2024-07-03 10:00:00', '2024-07-03 11:00:00', 4),
(8, 'Advances and Future of AI', '2024-07-03 14:00:00', '2024-07-03 15:00:00', 5);

-- --------------------------------------------------------

--
-- Estrutura da tabela `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `email` varchar(300) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` text NOT NULL,
  `apiKey` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Extraindo dados da tabela `users`
--

INSERT INTO `users` (`id`, `fullname`, `email`, `username`, `password`, `apiKey`) VALUES
(1, 'fullname', 'email', 'username', '$2y$10$Uw.Z4T.YdqnjdVeJQQ52v.8sCZfO5jYOXYlQzYwiz9hMWPYoi6PLa', ''),
(5, 'João Neto', 'joaoneto@gmail.com', 'joaoneto8', 'novaims', ''),
(6, 'Diogo Oliveira', 'diogo.mig.oli@gmail.com', 'diogoliveira', '$2y$10$ya7ZxJxh/5cSW/RA8FZG5Oy4IKO5Onww7IK4ZfdZJJZZt5q9Rac7i', '1f61dd8ccc531e0a934c35c3bdb2edcde7f2a616a46551');

--
-- Índices para tabelas despejadas
--

--
-- Índices para tabela `articles`
--
ALTER TABLE `articles`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `article_authors`
--
ALTER TABLE `article_authors`
  ADD PRIMARY KEY (`article_id`,`author_id`),
  ADD KEY `author_id` (`author_id`);

--
-- Índices para tabela `article_questions`
--
ALTER TABLE `article_questions`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `authors`
--
ALTER TABLE `authors`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `conferences`
--
ALTER TABLE `conferences`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`);

--
-- Índices para tabela `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT de tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `articles`
--
ALTER TABLE `articles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de tabela `article_questions`
--
ALTER TABLE `article_questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de tabela `authors`
--
ALTER TABLE `authors`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de tabela `conferences`
--
ALTER TABLE `conferences`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de tabela `rooms`
--
ALTER TABLE `rooms`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de tabela `sessions`
--
ALTER TABLE `sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de tabela `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restrições para despejos de tabelas
--

--
-- Limitadores para a tabela `article_authors`
--
ALTER TABLE `article_authors`
  ADD CONSTRAINT `article_authors_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`),
  ADD CONSTRAINT `article_authors_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
