package cat.command.commands;

import cat.command.Command;

public class ForCommand extends Command {
    public ForCommand() {
        super("ForCommand", "Sends a command or message determined times", "for <times> <command or message>", "for", "foreach");
    }
    @Override
    public void execute(String[] args){
        if(args.length > 2){
            String c = args[1];
            if(c.matches("\\d+")){
                int cn = Integer.parseInt(c);
                String command = args[2];
                if(!command.isEmpty()){
                    for (int i = 0; i < cn; i++) {
                        mc.thePlayer.sendChatMessage(command);
                    }
                }else{
                    chat("Syntax: " + this.syntax);
                }
            }else{
                chat("Cannot convert " + c + " to number.");
            }
        }else{
            chat("Syntax: " + this.syntax);
        }
    }
}
