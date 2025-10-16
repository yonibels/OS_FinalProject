package backend;

import java.util.*;
import java.util.List;

public class MemoryAllocationVisualizer {

    static class Partition {
        int location;
        int size;
        boolean allocated;
        String processName;
        int internalFragmentation;
        int jobSize;

        Partition(int location, int size) {
            this.location = location;
            this.size = size;
            this.allocated = false;
            this.processName = "";
            this.internalFragmentation = 0;
            this.jobSize = 0;
        }
    }
    
    static class Process {
        String name;
        int size;

        Process(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }

    static Scanner scanner = new Scanner(System.in);
    static List<Partition> memory = new ArrayList<>();
    static List<Process> processes = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("-------------------------------------------------");
        System.out.println("--     WELCOME TO MEMORY ALLOCATION VISUALIZER  --");
        System.out.println("-------------------------------------------------");
        System.out.println("--            PROGRAM PREPARED BY: BSIT 2-6     --");
        System.out.println("-------------------------------------------------");
        System.out.println("--                   MEMBERS                    --");
        System.out.println("--          SHAIRA RAGUINDIN MATA               --");
        System.out.println("--          KAITO ABENALES OISHI                --");
        System.out.println("--          ERIN LOUISE PANGILINAN              --");
        System.out.println("--          EUNICE RIZHEINE PASCUAL             --");
        System.out.println("-------------------------------------------------\n");

        inputMemory();
        inputProcesses();

        System.out.println("\nChoose Allocation Strategy:");
        System.out.println("1. First Fit");
        System.out.println("2. Best Fit");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = safeNextInt();

        if (choice == 1) {
            System.out.println("\n--- FIRST FIT ALLOCATION ---");
            firstFit();
        } else if (choice == 2) {
            System.out.println("\n--- BEST FIT ALLOCATION ---");
            bestFit();
        } else {
            System.out.println("Invalid choice. Exiting...");
            return;
        }

        printMemoryListTable();

        // menu options
        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Deallocate a process");
            System.out.println("2. Perform compaction");
            System.out.println("3. View memory table again");
            System.out.println("4. Allocate a new process");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int opt = safeNextInt();

            switch (opt) {
                case 1 -> deallocateProcess();
                case 2 -> compactMemory();
                case 3 -> printMemoryListTable();
                case 4 -> allocateNewProcess(); // NEW FEATURE
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // input methods

    static void inputMemory() {
        System.out.print("Enter number of memory blocks: ");
        int n = safeNextInt();
        for (int i = 0; i < n; i++) {
            System.out.print("Memory Block " + (i + 1) + " location: ");
            int location = safeNextInt();
            System.out.print("Memory Block " + (i + 1) + " size (KB): ");
            int size = safeNextInt();
            memory.add(new Partition(location, size));
        }
        memory.sort(Comparator.comparingInt(p -> p.location));
    }

    static void inputProcesses() {
        System.out.print("\nEnter number of processes (jobs): ");
        int n = safeNextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            System.out.print("Process " + (i + 1) + " name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "J" + (i + 1);
            System.out.print("Process " + name + " size (KB): ");
            int size = safeNextInt();
            scanner.nextLine();
            processes.add(new Process(name, size));
        }
    }

    // allocation methods

    static void firstFit() {
        for (Process p : processes) {
            boolean allocated = false;
            for (Partition part : memory) {
                if (!part.allocated && part.size >= p.size) {
                    allocatePartition(part, p);
                    allocated = true;
                    break;
                }
            }
            if (!allocated)
                System.out.println("Cannot allocate " + p.name + " (" + p.size + "K)");
        }
    }

    static void bestFit() {
        for (Process p : processes) {
            int bestIndex = -1;
            int smallestFit = Integer.MAX_VALUE;

            for (int i = 0; i < memory.size(); i++) {
                Partition part = memory.get(i);
                if (!part.allocated && part.size >= p.size && part.size < smallestFit) {
                    bestIndex = i;
                    smallestFit = part.size;
                }
            }

            if (bestIndex != -1)
                allocatePartition(memory.get(bestIndex), p);
            else
                System.out.println("Cannot allocate " + p.name + " (" + p.size + "K)");
        }
    }

    static void allocatePartition(Partition part, Process p) {
        part.allocated = true;
        part.processName = p.name;
        part.jobSize = p.size;
        part.internalFragmentation = part.size - p.size;
        System.out.println("Allocated " + p.name + " (" + p.size + "K) to block at location " + part.location);
    }

    // Nallocate new process menu option
    static void allocateNewProcess() {
        System.out.println("\n--- ALLOCATE NEW PROCESS ---");

        // Temporary lists to not overwrite existing memory unless user wants to expand
        System.out.print("Enter number of new memory blocks: ");
        int nBlocks = safeNextInt();
        for (int i = 0; i < nBlocks; i++) {
            System.out.print("Memory Block " + (i + 1) + " location: ");
            int location = safeNextInt();
            System.out.print("Memory Block " + (i + 1) + " size (KB): ");
            int size = safeNextInt();
            memory.add(new Partition(location, size));
        }
        memory.sort(Comparator.comparingInt(p -> p.location));

        System.out.print("\nEnter number of new processes (jobs): ");
        int n = safeNextInt();
        scanner.nextLine();
        List<Process> newProcesses = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.print("Process " + (i + 1) + " name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "J" + (i + 1);
            System.out.print("Process " + name + " size (KB): ");
            int size = safeNextInt();
            scanner.nextLine();
            newProcesses.add(new Process(name, size));
        }

        System.out.println("\nChoose Allocation Strategy:");
        System.out.println("1. First Fit");
        System.out.println("2. Best Fit");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = safeNextInt();

        if (choice == 1) {
            System.out.println("\n--- FIRST FIT ALLOCATION ---");
            for (Process p : newProcesses) {
                boolean allocated = false;
                for (Partition part : memory) {
                    if (!part.allocated && part.size >= p.size) {
                        allocatePartition(part, p);
                        allocated = true;
                        break;
                    }
                }
                if (!allocated)
                    System.out.println("Cannot allocate " + p.name + " (" + p.size + "K)");
            }
        } else if (choice == 2) {
            System.out.println("\n--- BEST FIT ALLOCATION ---");
            for (Process p : newProcesses) {
                int bestIndex = -1;
                int smallestFit = Integer.MAX_VALUE;

                for (int i = 0; i < memory.size(); i++) {
                    Partition part = memory.get(i);
                    if (!part.allocated && part.size >= p.size && part.size < smallestFit) {
                        bestIndex = i;
                        smallestFit = part.size;
                    }
                }

                if (bestIndex != -1)
                    allocatePartition(memory.get(bestIndex), p);
                else
                    System.out.println("Cannot allocate " + p.name + " (" + p.size + "K)");
            }
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        printMemoryListTable();
    }

    // deallocation
    static void deallocateProcess() {
        System.out.print("Enter process name to deallocate: ");
        scanner.nextLine();
        String name = scanner.nextLine().trim();

        boolean found = false;
        for (Partition part : memory) {
            if (part.allocated && part.processName.equalsIgnoreCase(name)) {
                part.allocated = false;
                part.processName = "";
                part.jobSize = 0;
                part.internalFragmentation = 0;
                found = true;
                System.out.println("Deallocated process " + name + ".");
                break;
            }
        }

        if (!found)
            System.out.println("Process " + name + " not found or not allocated.");
    }

    // compaction
    static void compactMemory() {
        System.out.println("\nPerforming compaction...");
        List<Partition> allocatedBlocks = new ArrayList<>();
        int totalFreeSpace = 0;

        for (Partition p : memory) {
            if (p.allocated)
                allocatedBlocks.add(p);
            else
                totalFreeSpace += p.size;
        }

        memory.clear();
        int nextLocation = 0;
        for (Partition p : allocatedBlocks) {
            memory.add(new Partition(nextLocation, p.size));
            Partition newPart = memory.get(memory.size() - 1);
            newPart.allocated = true;
            newPart.processName = p.processName;
            newPart.jobSize = p.jobSize;
            newPart.internalFragmentation = p.internalFragmentation;
            nextLocation += p.size;
        }

        if (totalFreeSpace > 0)
            memory.add(new Partition(nextLocation, totalFreeSpace));

        System.out.println("Compaction completed. All free spaces merged at the end.");
        printMemoryListTable();
    }

    // output table
    static void printMemoryListTable() {
        System.out.println("\nMemory List:");
        System.out.printf("%-15s %-20s %-12s %-12s %-10s %-15s\n",
                "Memory location", "Memory block size", "Job number", "Job size", "Status", "Internal frag");

        int totalAvailable = 0;
        int totalUsed = 0;

        for (Partition p : memory) {
            String job = p.allocated ? p.processName : "-";
            String jobSize = p.allocated ? p.jobSize + "K" : "-";
            String status = p.allocated ? "Busy" : "Free";
            String frag = p.allocated ? p.internalFragmentation + "K" : "-";

            System.out.printf("%-15d %-20s %-12s %-12s %-10s %-15s\n",
                    p.location, p.size + "K", job, jobSize, status, frag);

            totalAvailable += p.size;
            if (p.allocated) totalUsed += p.jobSize;
        }

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-12s %-12s %-10s %-15s\n",
                "", totalAvailable + "K (avail)", "", totalUsed + "K (used)", "", "");
    }

    static int safeNextInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.print("Invalid input. Enter a number: ");
                scanner.nextLine();
            }
        }
    }
}