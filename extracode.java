 try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 FileWriter fileWriter = new FileWriter(outputFile)) {
                
                String line;
                while ((line = in.readLine()) != null) {
                    fileWriter.write(line);
                    fileWriter.write(System.lineSeparator());
                }
            }

            System.out.println("Content saved to " + outputFile);
