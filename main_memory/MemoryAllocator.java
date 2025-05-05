package main_memory;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class MemoryAllocator{
    int MemoryBlockSizeLength = 10;
    int MemoryBlockSize[] = GenerateMemoryBlockSize();
    int ProcessSizeLength = 8;
    int ProcessSize[] = GenerateProcessSize();
    int MemoryAllocation[] = new int[ProcessSizeLength];

    public void Run(){
        String Input;
        Scanner InputScanner;

        InputScanner = new Scanner(System.in);

        while (true){
            System.out.print("Memory Block Sizes: ");

            PrintMemoryBlockSize();

            System.out.print("\nProcess Sizes: ");

            PrintProcessSize();

            System.out.println("\nAllocation Algorithm\n1.) First Fit\n2.) Next Fit\n3.) Best Fit\n4.) Worst Fit\n5.) Regenerate Memory Block Sizes\n6.) Regenerate Process Sizes");

            Input = InputScanner.nextLine();
            Input = Input.strip();

            if (Input.equals("1")){
                FirstFit();

                break;
            }
            else if (Input.equals("2")){
                NextFit();

                break;
            }
            else if (Input.equals("3")){
                BestFit();

                break;
            }
            else if (Input.equals("4")){
                WorstFit();

                break;
            }
            else if (Input.equals("5")){
                MemoryBlockSize = GenerateMemoryBlockSize();
            }
            else if (Input.equals("6")){
                ProcessSize = GenerateProcessSize();
            }
        }
    }

    void FirstFit(){
        for (int i = 0; i < ProcessSizeLength; i++){
            MemoryAllocation[i] = -1;
        }

        for (int i = 0; i < ProcessSizeLength; i++){
            for (int j = 0; j < MemoryBlockSizeLength; j++){
                if (MemoryBlockSize[j] >= ProcessSize[i]){
                    MemoryAllocation[i] = j;

                    MemoryBlockSize[j] -= ProcessSize[i];

                    break;
                }
            }
        }

        PrintStats();
    }

    void NextFit(){
        int CurrentLocation = 0;
        int EndPoint = (MemoryBlockSizeLength - 1);

        for (int i = 0; i < ProcessSizeLength; i++){
            MemoryAllocation[i] = -1;
        }

        for (int i = 0; i < ProcessSizeLength; i++){
            while (CurrentLocation < MemoryBlockSizeLength){
                if (MemoryBlockSize[CurrentLocation] >= ProcessSize[i]){
                    MemoryAllocation[i] = CurrentLocation;

                    MemoryBlockSize[CurrentLocation] -= ProcessSize[i];

                    EndPoint = ((CurrentLocation - 1) % MemoryBlockSizeLength);

                    break;
                }

                if (CurrentLocation == EndPoint){
                    EndPoint = ((CurrentLocation - 1) % MemoryBlockSizeLength);

                    break;
                }

                CurrentLocation = ((CurrentLocation + 1) % MemoryBlockSizeLength);
            }
        }

        PrintStats();
    }

    void BestFit(){
        MemoryBlockSize = SortMemoryBlockSizeMinimum();

        System.out.print("Ordered Memory Block Sizes: ");

        PrintMemoryBlockSize();

        System.out.println();

        for (int i = 0; i < ProcessSizeLength; i++){
            MemoryAllocation[i] = -1;
        }

        for (int i = 0; i < ProcessSizeLength; i++){
            for (int j = 0; j < MemoryBlockSizeLength; j++){
                if (MemoryBlockSize[j] >= ProcessSize[i]){
                    MemoryAllocation[i] = j;

                    MemoryBlockSize[j] -= ProcessSize[i];

                    break;
                }
            }
        }

        PrintStats();
    }

    void WorstFit(){
        MemoryBlockSize = SortMemoryBlockSizeMaximum();

        System.out.print("Ordered Memory Block Sizes: ");

        PrintMemoryBlockSize();

        System.out.println();

        for (int i = 0; i < ProcessSizeLength; i++){
            MemoryAllocation[i] = -1;
        }

        for (int i = 0; i < ProcessSizeLength; i++){
            for (int j = 0; j < MemoryBlockSizeLength; j++){
                if (MemoryBlockSize[j] >= ProcessSize[i]){
                    MemoryAllocation[i] = j;

                    MemoryBlockSize[j] -= ProcessSize[i];

                    break;
                }
            }
        }

        PrintStats();
    }

    int[] GenerateMemoryBlockSize(){
        int[] BlockSize = new int[MemoryBlockSizeLength];

        for (int i = 0; i < MemoryBlockSizeLength; i++){
            BlockSize[i] = ThreadLocalRandom.current().nextInt(10, 501);
        }

        return BlockSize;
    }

    int[] GenerateProcessSize(){
        int[] ProcessSize = new int[ProcessSizeLength];

        for (int i = 0; i < ProcessSizeLength; i++){
            ProcessSize[i] = ThreadLocalRandom.current().nextInt(10, 501);
        }

        return ProcessSize;
    }

    void PrintMemoryBlockSize(){
        for (int i = 0; i < MemoryBlockSizeLength; i++){
            System.out.print(MemoryBlockSize[i] + " ");
        }
    }

    void PrintProcessSize(){
        for (int i = 0; i < ProcessSizeLength; i++){
            System.out.print(ProcessSize[i] + " ");
        }
    }

    void PrintStats(){
        System.out.println("Process\tSize\tBlock");

        for (int i = 0; i < ProcessSizeLength; i++){
            System.out.print((i + 1) + "\t\t" + ProcessSize[i] + "\t\t");

            if (MemoryAllocation[i] != -1){
                System.out.print(MemoryAllocation[i] + "\n");
            }
            else{
                System.out.print("Not Allocated\n");
            }
        }

        System.out.println("\nNew Memory Block Sizes:");

        for (int i = 0; i < MemoryBlockSizeLength; i++){
            System.out.print(MemoryBlockSize[i] + " ");
        }

        System.out.println();
    }

    int[] SortMemoryBlockSizeMaximum(){
        int[] BlockSize = new int[MemoryBlockSizeLength];
        int MaxValue;
        int Index;

        for (int i = 0; i < MemoryBlockSizeLength; i++){
            MaxValue = Integer.MIN_VALUE;
            Index = 0;

            for (int j = 0; j < MemoryBlockSizeLength; j++){
                if (MemoryBlockSize[j] > MaxValue){
                    MaxValue = MemoryBlockSize[j];
                    Index = j;
                }
            }

            BlockSize[i] = MemoryBlockSize[Index];

            MemoryBlockSize[Index] = Integer.MIN_VALUE;
        }

        return BlockSize;
    }

    int[] SortMemoryBlockSizeMinimum(){
        int[] BlockSize = new int[MemoryBlockSizeLength];
        int MinValue;
        int Index;

        for (int i = 0; i < MemoryBlockSizeLength; i++){
            MinValue = Integer.MAX_VALUE;
            Index = 0;

            for (int j = 0; j < MemoryBlockSizeLength; j++){
                if (MemoryBlockSize[j] < MinValue){
                    MinValue = MemoryBlockSize[j];
                    Index = j;
                }
            }

            BlockSize[i] = MemoryBlockSize[Index];

            MemoryBlockSize[Index] = Integer.MAX_VALUE;
        }

        return BlockSize;
    }
}
