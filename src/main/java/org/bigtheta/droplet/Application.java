package org.bigtheta.droplet;

/**
 *
 */
public class Application {

    public static final String MSG_HELP = "type:\n" +
            "\t* list - show all droplets\n" +
            "\t* create <name>  - create new droplet with given name\n" +
            "\t* delete <number> - delete droplet with given number\n";

    public static void main(String[] args) {

        Processor processor = new Processor();
        if (args.length == 0) {
            System.out.println(MSG_HELP);
            return;
        }
        System.out.println(processor.process(args));
    }
}
